package pokecube.mobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.versions.forge.ForgeVersion;
import pokecube.adventures.utils.DBLoader;
import pokecube.core.PokecubeCore;
import pokecube.core.PokecubeItems;
import pokecube.core.database.Database;
import pokecube.core.database.Database.EnumDatabase;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.PokedexEntry.EvolutionData;
import pokecube.core.database.abilities.p.Pickup;
import pokecube.core.database.recipes.XMLRecipeHandler;
import pokecube.core.database.stats.CaptureStats;
import pokecube.core.database.stats.EggStats;
import pokecube.core.database.stats.StatsCollector;
import pokecube.core.events.CaptureEvent.Post;
import pokecube.core.events.CaptureEvent.Pre;
import pokecube.core.events.handlers.EventsHandler;
import pokecube.core.events.onload.InitDatabase;
import pokecube.core.events.onload.RegisterPokecubes;
import pokecube.core.events.onload.RegisterPokemobsEvent;
import pokecube.core.events.pokemob.EvolveEvent;
import pokecube.core.handlers.ItemGenerator;
import pokecube.core.interfaces.IPokecube;
import pokecube.core.interfaces.IPokecube.DefaultPokecubeBehavior;
import pokecube.core.interfaces.IPokecube.NormalPokecubeBehavoir;
import pokecube.core.interfaces.IPokecube.PokecubeBehavior;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.Stats;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.GeneralStates;
import pokecube.core.items.berries.BerryManager;
import pokecube.core.items.pokecubes.EntityPokecube;
import pokecube.core.items.pokecubes.PokecubeManager;
import pokecube.core.utils.Tools;
import pokecube.mobs.client.render.ModelWrapperSpinda;
import pokecube.mobs.client.smd.SMDModel;
import pokecube.modelloader.CommonProxy;
import pokecube.modelloader.IMobProvider;
import pokecube.modelloader.ModPokecubeML;
import pokecube.modelloader.client.render.ModelWrapperEvent;
import thut.api.maths.Vector3;
import thut.core.client.ClientProxy;
import thut.core.client.render.model.ModelFactory;
import thut.lib.CompatWrapper;

@Mod(modid = PokecubeMobs.MODID, name = "Pokecube Mobs", version = Reference.VERSION, dependencies = "required-after:pokecube;required-after:pokecube_adventures", updateJSON = PokecubeMobs.UPDATEURL, acceptableRemoteVersions = Reference.MINVERSION, acceptedMinecraftVersions = Reference.MCVERSIONS)
public class PokecubeMobs implements IMobProvider
{
    public static class UpdateNotifier
    {
        public UpdateNotifier()
        {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onPlayerJoin(TickEvent.PlayerTickEvent event)
        {
            if (!"".equals(Loader.instance().activeModContainer().getMetadata().parent))
            {
                MinecraftForge.EVENT_BUS.unregister(this);
                return;
            }
            if (event.player.getEntityWorld().isRemote
                    && event.player == FMLClientHandler.instance().getClientPlayerEntity())
            {
                MinecraftForge.EVENT_BUS.unregister(this);
                Object o = Loader.instance().getIndexedModList().get(PokecubeMobs.MODID);
                CheckResult result = ForgeVersion.getResult(((ModContainer) o));
                if (result.status == Status.OUTDATED)
                {
                    ITextComponent mess = ClientProxy.getOutdatedMessage(result, "Pokecube Mobs");
                    (event.player).sendMessage(mess);
                }
            }
        }
    }

    Map<PokedexEntry, Integer> genMap     = Maps.newHashMap();
    Set<PokedexEntry>          missingnos = Sets.newHashSet();
    public static final String MODID      = "pokecube_mobs";
    public static final String UPDATEURL  = "https://raw.githubusercontent.com/Pokecube-Development/Pokecube-Mobs/master/versions.json";

    public PokecubeMobs()
    {
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_1/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_2/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_3/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_4/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_5/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_6/entity/models/");
        ModPokecubeML.scanPaths.add("assets/pokecube_mobs/gen_7/entity/models/");
        CommonProxy.registerModelProvider(MODID, this);

        ItemGenerator.variants.add("waterstone");
        ItemGenerator.variants.add("firestone");
        ItemGenerator.variants.add("leafstone");
        ItemGenerator.variants.add("thunderstone");
        ItemGenerator.variants.add("moonstone");
        ItemGenerator.variants.add("sunstone");
        ItemGenerator.variants.add("shinystone");
        ItemGenerator.variants.add("ovalstone");
        ItemGenerator.variants.add("everstone");
        ItemGenerator.variants.add("duskstone");
        ItemGenerator.variants.add("dawnstone");
        ItemGenerator.variants.add("kingsrock");
        ItemGenerator.variants.add("dubiousdisc");
        ItemGenerator.variants.add("electirizer");
        ItemGenerator.variants.add("magmarizer");
        ItemGenerator.variants.add("reapercloth");
        ItemGenerator.variants.add("prismscale");
        ItemGenerator.variants.add("protector");
        ItemGenerator.variants.add("upgrade");
        ItemGenerator.variants.add("metalcoat");

        ItemGenerator.variants.add("megastone");
        ItemGenerator.variants.add("shiny_charm");
        ItemGenerator.variants.add("omegaorb");
        ItemGenerator.variants.add("alphaorb");
        ItemGenerator.variants.add("aerodactylmega");
        ItemGenerator.variants.add("abomasnowmega");
        ItemGenerator.variants.add("absolmega");
        ItemGenerator.variants.add("aggronmega");
        ItemGenerator.variants.add("alakazammega");
        ItemGenerator.variants.add("altariamega");
        ItemGenerator.variants.add("ampharosmega");
        ItemGenerator.variants.add("audinomega");
        ItemGenerator.variants.add("banettemega");
        ItemGenerator.variants.add("beedrillmega");
        ItemGenerator.variants.add("blastoisemega");
        ItemGenerator.variants.add("blazikenmega");
        ItemGenerator.variants.add("cameruptmega");
        ItemGenerator.variants.add("charizardmega-y");
        ItemGenerator.variants.add("charizardmega-x");
        ItemGenerator.variants.add("dianciemega");
        ItemGenerator.variants.add("gallademega");
        ItemGenerator.variants.add("garchompmega");
        ItemGenerator.variants.add("gardevoirmega");
        ItemGenerator.variants.add("gengarmega");
        ItemGenerator.variants.add("glaliemega");
        ItemGenerator.variants.add("gyaradosmega");
        ItemGenerator.variants.add("heracrossmega");
        ItemGenerator.variants.add("houndoommega");
        ItemGenerator.variants.add("kangaskhanmega");
        ItemGenerator.variants.add("latiasmega");
        ItemGenerator.variants.add("latiosmega");
        ItemGenerator.variants.add("lopunnymega");
        ItemGenerator.variants.add("lucariomega");
        ItemGenerator.variants.add("manectricmega");
        ItemGenerator.variants.add("mawilemega");
        ItemGenerator.variants.add("medichammega");
        ItemGenerator.variants.add("metagrossmega");
        ItemGenerator.variants.add("mewtwomega-y");
        ItemGenerator.variants.add("mewtwomega-x");
        ItemGenerator.variants.add("pidgeotmega");
        ItemGenerator.variants.add("pinsirmega");
        ItemGenerator.variants.add("sableyemega");
        ItemGenerator.variants.add("salamencemega");
        ItemGenerator.variants.add("sceptilemega");
        ItemGenerator.variants.add("scizormega");
        ItemGenerator.variants.add("sharpedomega");
        ItemGenerator.variants.add("slowbromega");
        ItemGenerator.variants.add("steelixmega");
        ItemGenerator.variants.add("swampertmega");
        ItemGenerator.variants.add("tyranitarmega");
        ItemGenerator.variants.add("venusaurmega");

        ItemGenerator.fossilVariants.add("omanyte");
        ItemGenerator.fossilVariants.add("kabuto");
        ItemGenerator.fossilVariants.add("aerodactyl");
        ItemGenerator.fossilVariants.add("lileep");
        ItemGenerator.fossilVariants.add("anorith");
        ItemGenerator.fossilVariants.add("cranidos");
        ItemGenerator.fossilVariants.add("shieldon");
        ItemGenerator.fossilVariants.add("archen");
        ItemGenerator.fossilVariants.add("tirtouga");
        ItemGenerator.fossilVariants.add("tyrunt");
        ItemGenerator.fossilVariants.add("amaura");

        MinecraftForge.EVENT_BUS.register(this);
        BerryHelper.initBerries();
        if (Loader.isModLoaded("thut_wearables")) MegaWearablesHelper.initExtraWearables();
        DBLoader.trainerDatabases.add("trainers.xml");
        DBLoader.tradeDatabases.add("trades.xml");
        MiscItemHelper.init();

        // Register smd format for models
        ModelFactory.registerIModel("smd", SMDModel.class);
        ModelFactory.registerIModel("SMD", SMDModel.class);
    }

    @EventHandler
    public void preInit(FMLCommonSetupEvent event)
    {
        Configuration config = PokecubeCore.instance.getPokecubeConfig(event);
        config.load();
        String var = config.getString("pickuploottable", Configuration.CATEGORY_GENERAL, "",
                "If Set, this is the loot table that pickup will use.");
        Pickup.useLootTable = config.getBoolean("usePickupTable", Configuration.CATEGORY_GENERAL, true,
                "whether pickup uses the built in default loot table.");
        if (!var.isEmpty())
        {
            Pickup.lootTable = new ResourceLocation(var);
        }
        config.save();
        if (event.getSide() == Dist.CLIENT)
        {
            new UpdateNotifier();
        }
    }

    @SubscribeEvent
    public void RegisterPokemobsEvent(RegisterPokemobsEvent.Register event)
    {
        for (PokedexEntry entry : Database.getSortedFormes())
        {
            // Came from a resourcepack
            if (entry.getModId() == null) continue;
            if (entry.getModId().equals(PokecubeMissingno.MODID))
            {
                entry.setModId(MODID);
                missingnos.add(entry);
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandGenStuff());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void initModel(ModelWrapperEvent evt)
    {
        if (evt.name.equalsIgnoreCase("spinda"))
        {
            evt.wrapper = new ModelWrapperSpinda(evt.wrapper.model, evt.wrapper.renderer);
        }
    }

    @Override
    public String getModelDirectory(PokedexEntry entry)
    {
        int gen = getGen(entry);
        switch (gen)
        {
        case 1:
            return "gen_1/entity/models/";
        case 2:
            return "gen_2/entity/models/";
        case 3:
            return "gen_3/entity/models/";
        case 4:
            return "gen_4/entity/models/";
        case 5:
            return "gen_5/entity/models/";
        case 6:
            return "gen_6/entity/models/";
        case 7:
            return "gen_7/entity/models/";
        }
        return "entity/models/";
    }

    private int getGen(PokedexEntry entry)
    {
        int gen;
        if (missingnos.contains(entry)) return 0;
        if (genMap.containsKey(entry))
        {
            gen = genMap.get(entry);
        }
        else
        {
            gen = entry.getGen();
            PokedexEntry real = entry;
            if (entry.getBaseForme() != null) entry = entry.getBaseForme();
            for (EvolutionData d : entry.getEvolutions())
            {
                int gen1 = d.evolution.getGen();
                if (genMap.containsKey(d.evolution))
                {
                    gen1 = genMap.get(d.evolution);
                }
                if (gen1 < gen)
                {
                    gen = gen1;
                }
                for (EvolutionData d1 : d.evolution.getEvolutions())
                {
                    gen1 = d1.evolution.getGen();
                    if (genMap.containsKey(d1.evolution))
                    {
                        gen1 = genMap.get(d1.evolution);
                    }
                    if (d.evolution == entry && gen1 < gen)
                    {
                        gen = gen1;
                    }
                }
            }
            for (PokedexEntry e : Database.getSortedFormes())
            {
                int gen1 = e.getGen();
                if (genMap.containsKey(e))
                {
                    gen1 = genMap.get(e);
                }
                for (EvolutionData d : e.getEvolutions())
                {
                    if (d.evolution == entry && gen1 < gen)
                    {
                        gen = gen1;
                    }
                }
            }
            genMap.put(real, gen);
        }
        return gen;
    }

    @Override
    public String getTextureDirectory(PokedexEntry entry)
    {
        int gen = getGen(entry);
        switch (gen)
        {
        case 1:
            return "gen_1/entity/textures/";
        case 2:
            return "gen_2/entity/textures/";
        case 3:
            return "gen_3/entity/textures/";
        case 4:
            return "gen_4/entity/textures/";
        case 5:
            return "gen_5/entity/textures/";
        case 6:
            return "gen_6/entity/textures/";
        case 7:
            return "gen_7/entity/textures/";
        }
        return "entity/textures/";
    }

    @Override
    public Object getMod()
    {
        return this;
    }

    @SubscribeEvent
    public void registerPokecubes(RegisterPokecubes event)
    {
        final PokecubeHelper helper = new PokecubeHelper();
        PokecubeBehavior.DEFAULTCUBE = new ResourceLocation("pokecube", "poke");

        event.behaviors.add(new NormalPokecubeBehavoir(1).setRegistryName(PokecubeBehavior.DEFAULTCUBE));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "great"));
        event.behaviors.add(new NormalPokecubeBehavoir(2).setRegistryName("pokecube", "ultra"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "master"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.dusk(mob);
            }
        }.setRegistryName("pokecube", "dusk"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.quick(mob);
            }
        }.setRegistryName("pokecube", "quick"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.timer(mob);
            }
        }.setRegistryName("pokecube", "timer"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.net(mob);
            }
        }.setRegistryName("pokecube", "net"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.nest(mob);
            }
        }.setRegistryName("pokecube", "nest"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.dive(mob);
            }
        }.setRegistryName("pokecube", "dive"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.premier(mob);
            }
        }.setRegistryName("pokecube", "premier"));
        event.behaviors.add(new NormalPokecubeBehavoir(1).setRegistryName("pokecube", "cherish"));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "safari"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.level(mob);
            }
        }.setRegistryName("pokecube", "level"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.lure(mob);
            }
        }.setRegistryName("pokecube", "lure"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.moon(mob);
            }
        }.setRegistryName("pokecube", "moon"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                mob.addHappiness(200 - mob.getHappiness());
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 1;
            }
        }.setRegistryName("pokecube", "friend"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.love(mob);
            }
        }.setRegistryName("pokecube", "love"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public int getAdditionalBonus(IPokemob mob)
            {
                return helper.heavy(mob);
            }
        }.setRegistryName("pokecube", "heavy"));
        event.behaviors.add(new DefaultPokecubeBehavior()
        {
            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return helper.fast(mob);
            }
        }.setRegistryName("pokecube", "fast"));
        event.behaviors.add(new NormalPokecubeBehavoir(1.5).setRegistryName("pokecube", "sport"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public void onUpdate(IPokemob mob)
            {
                helper.luxury(mob);
            }
        }.setRegistryName("pokecube", "luxury"));
        event.behaviors.add(new NormalPokecubeBehavoir(1)
        {
            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                mob.getEntity().setHealth(mob.getEntity().getMaxHealth());
                mob.healStatus();
            }
        }.setRegistryName("pokecube", "heal"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "park"));
        event.behaviors.add(new NormalPokecubeBehavoir(255).setRegistryName("pokecube", "dream"));

        PokecubeBehavior snag = new PokecubeBehavior()
        {

            @Override
            public void onPostCapture(Post evt)
            {
                IPokemob mob = evt.caught;
                if (mob != null) evt.pokecube.entityDropItem(PokecubeManager.pokemobToItem(mob), 1.0F);
                evt.setCanceled(true);
            }

            @Override
            public void onPreCapture(Pre evt)
            {
                boolean tameSnag = !evt.caught.isPlayerOwned() && evt.caught.getGeneralState(GeneralStates.TAMED);

                if (evt.caught.isShadow())
                {
                    EntityPokecube cube = (EntityPokecube) evt.pokecube;

                    IPokemob mob = CapabilityPokemob.getPokemobFor(
                            PokecubeCore.instance.createPokemob(evt.caught.getPokedexEntry(), cube.getEntityWorld()));
                    cube.tilt = Tools.computeCatchRate(mob, 1);
                    cube.time = cube.tilt * 20;

                    if (!tameSnag) evt.caught.setPokecube(evt.filledCube);

                    cube.setItemEntityStack(PokecubeManager.pokemobToItem(evt.caught));
                    PokecubeManager.setTilt(cube.getItemEntity(), cube.tilt);
                    Vector3.getNewVector().set(evt.pokecube).moveEntity(cube);
                    evt.caught.getEntity().setDead();
                    cube.motionX = cube.motionZ = 0;
                    cube.motionY = 0.1;
                    cube.getEntityWorld().spawnEntity(cube.copy());
                    evt.pokecube.setDead();
                }
                evt.setCanceled(true);
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 0;
            }
        };

        PokecubeBehavior repeat = new PokecubeBehavior()
        {
            @Override
            public void onPostCapture(Post evt)
            {

            }

            @Override
            public void onPreCapture(Pre evt)
            {
                if (evt.getResult() == Result.DENY) return;

                EntityPokecube cube = (EntityPokecube) evt.pokecube;

                IPokemob mob = CapabilityPokemob.getPokemobFor(
                        PokecubeCore.instance.createPokemob(evt.caught.getPokedexEntry(), cube.getEntityWorld()));
                Vector3 v = Vector3.getNewVector();
                Entity thrower = cube.shootingEntity;
                int has = CaptureStats.getTotalNumberOfPokemobCaughtBy(thrower.getUniqueID(), mob.getPokedexEntry());
                has = has + EggStats.getTotalNumberOfPokemobHatchedBy(thrower.getUniqueID(), mob.getPokedexEntry());
                double rate = has > 0 ? 3 : 1;
                cube.tilt = Tools.computeCatchRate(mob, rate);
                cube.time = cube.tilt * 20;
                evt.caught.setPokecube(evt.filledCube);
                cube.setItemEntityStack(PokecubeManager.pokemobToItem(evt.caught));
                PokecubeManager.setTilt(cube.getItemEntity(), cube.tilt);
                v.set(evt.pokecube).moveEntity(cube);
                v.moveEntity(mob.getEntity());
                evt.caught.getEntity().setDead();
                cube.motionX = cube.motionZ = 0;
                cube.motionY = 0.1;
                cube.getEntityWorld().spawnEntity(cube.copy());
                evt.setCanceled(true);
                evt.pokecube.setDead();
            }

            @Override
            public double getCaptureModifier(IPokemob mob)
            {
                return 0;
            }

        };

        event.behaviors.add(snag.setRegistryName("pokecube", "snag"));
        event.behaviors.add(repeat.setRegistryName("pokecube", "repeat"));
    }

    @SubscribeEvent
    public void makeShedinja(EvolveEvent.Post evt)
    {
        Entity owner;
        if ((owner = evt.mob.getPokemonOwner()) instanceof PlayerEntity)
        {
            makeShedinja(evt.mob, (PlayerEntity) owner);
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingUpdateEvent evt)
    {
        IPokemob shuckle = CapabilityPokemob.getPokemobFor(evt.getMobEntity());
        if (shuckle != null && shuckle.getPokedexNb() == 213)
        {
            if (evt.getMobEntity().getEntityWorld().isRemote) return;

            ItemStack item = evt.getMobEntity().getHeldItemMainhand();
            if (!CompatWrapper.isValid(item)) return;
            Item itemId = item.getItem();
            boolean berry = item.isItemEqual(BerryManager.getBerryItem("oran"));
            Random r = new Random();
            if (berry && r.nextGaussian() > EventsHandler.juiceChance)
            {
                if (shuckle.getPokemonOwner() != null)
                {
                    String message = "A sweet smell is coming from "
                            + shuckle.getPokemonDisplayName().getFormattedText();
                    ((PlayerEntity) shuckle.getPokemonOwner()).sendMessage(new StringTextComponent(message));
                }
                shuckle.setHeldItem(new ItemStack(PokecubeItems.berryJuice));
                return;
            }
            berry = itemId == PokecubeItems.berryJuice;
            if (berry && (r.nextGaussian() > EventsHandler.candyChance))
            {
                ItemStack candy = PokecubeItems.makeCandyStack();
                if (!CompatWrapper.isValid(candy)) return;

                if (shuckle.getPokemonOwner() != null && shuckle.getPokemonOwner() instanceof PlayerEntity)
                {
                    String message = "The smell coming from " + shuckle.getPokemonDisplayName().getFormattedText()
                            + " has changed";
                    ((PlayerEntity) shuckle.getPokemonOwner()).sendMessage(new StringTextComponent(message));
                }
                shuckle.setHeldItem(candy);
                return;
            }
        }
    }

    @SubscribeEvent
    public void evolveTyrogue(EvolveEvent.Pre evt)
    {
        if (evt.mob.getPokedexEntry() == Database.getEntry("Tyrogue"))
        {
            int atk = evt.mob.getStat(Stats.ATTACK, false);
            int def = evt.mob.getStat(Stats.DEFENSE, false);
            if (atk > def) evt.forme = Database.getEntry("Hitmonlee");
            else if (def > atk) evt.forme = Database.getEntry("Hitmonchan");
            else evt.forme = Database.getEntry("Hitmontop");
        }
    }

    void makeShedinja(IPokemob evo, PlayerEntity player)
    {
        if (evo.getPokedexEntry() == Database.getEntry("ninjask"))
        {
            InventoryPlayer inv = player.inventory;
            boolean hasCube = false;
            boolean hasSpace = false;
            ItemStack cube = ItemStack.EMPTY;
            int m = -1;
            for (int n = 0; n < inv.getSizeInventory(); n++)
            {
                ItemStack item = inv.getStackInSlot(n);
                if (item == ItemStack.EMPTY) hasSpace = true;
                ResourceLocation key = PokecubeItems.getCubeId(item);
                if (!hasCube && key != null && IPokecube.BEHAVIORS.containsKey(key) && !PokecubeManager.isFilled(item))
                {
                    hasCube = true;
                    cube = item;
                    m = n;
                }
                if (hasCube && hasSpace) break;

            }
            if (hasCube && hasSpace)
            {
                Entity pokemon = PokecubeMod.core.createPokemob(Database.getEntry("shedinja"), player.getEntityWorld());
                if (pokemon != null)
                {
                    ItemStack mobCube = cube.copy();
                    mobCube.setCount(1);
                    IPokemob poke = CapabilityPokemob.getPokemobFor(pokemon);
                    poke.setPokecube(mobCube);
                    poke.setPokemonOwner(player);
                    poke.setExp(Tools.levelToXp(poke.getExperienceMode(), 20), true);
                    poke.getEntity().setHealth(poke.getEntity().getMaxHealth());
                    ItemStack shedinja = PokecubeManager.pokemobToItem(poke);
                    StatsCollector.addCapture(poke);
                    cube.shrink(1);
                    if (cube.isEmpty()) inv.setInventorySlotContents(m, ItemStack.EMPTY);
                    inv.addItemStackToInventory(shedinja);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void registerDatabases(InitDatabase.Pre evt)
    {
        writeDefaultConfig();

        Database.addDatabase("pokemobs_pokedex.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_spawns.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_drops.json", EnumDatabase.POKEMON);
        Database.addDatabase("pokemobs_interacts.json", EnumDatabase.POKEMON);

        Database.addDatabase("moves.json", EnumDatabase.MOVES);

        Database.addDatabase("spawns.json", EnumDatabase.BERRIES);
    }

    static String CONFIGLOC  = Database.CONFIGLOC;
    static String DBLOCATION = Database.DBLOCATION;

    private static void writeDefaultConfig()
    {
        try
        {
            File temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_mobs");
            copyDatabaseFile("moves.json", Database.FORCECOPY);
            copyDatabaseFile("animations.json", Database.FORCECOPY);

            CONFIGLOC = CONFIGLOC + "pokemobs" + File.separator;
            temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            DBLOCATION = DBLOCATION + "pokemobs/";
            copyDatabaseFile("pokemobs_pokedex.json", Database.FORCECOPY);
            copyDatabaseFile("pokemobs_spawns.json", Database.FORCECOPY);
            copyDatabaseFile("pokemobs_drops.json", Database.FORCECOPY);
            copyDatabaseFile("pokemobs_interacts.json", Database.FORCECOPY);

            CONFIGLOC = CONFIGLOC.replace("pokemobs", "berries");
            DBLOCATION = DBLOCATION.replace("pokemobs", "berries");
            copyDatabaseFile("spawns.json", PokecubeMod.core.getConfig().forceBerries);

            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_mobs");
            CONFIGLOC = Database.CONFIGLOC;
            copyDatabaseFile("pokecubes_recipes.json", Database.FORCECOPYRECIPES);
            copyDatabaseFile("pokemob_item_recipes.json", Database.FORCECOPYRECIPES);
            XMLRecipeHandler.recipeFiles.add("pokecubes_recipes");
            XMLRecipeHandler.recipeFiles.add("pokemob_item_recipes");
            DBLOCATION = Database.DBLOCATION.replace("pokecube", "pokecube_adventures");
            CONFIGLOC = Database.CONFIGLOC.replace("database", "trainers");
            temp = new File(Database.CONFIGLOC);
            if (!temp.exists())
            {
                temp.mkdirs();
            }
            copyDatabaseFile("trainers.xml", DBLoader.FORCECOPY);
            copyDatabaseFile("trades.xml", DBLoader.FORCECOPY);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static void copyDatabaseFile(String name, boolean force)
    {
        File temp1 = new File(CONFIGLOC + name);
        if (temp1.exists() && !force)
        {
            PokecubeMod.log("Not Overwriting old database: " + temp1);
            return;
        }
        ArrayList<String> rows = getFile(DBLOCATION + name);
        int n = 0;
        try
        {
            File file = new File(CONFIGLOC + name);
            file.getParentFile().mkdirs();
            if (PokecubeMod.debug) PokecubeMod.log("Copying Database File: " + file);
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            for (int i = 0; i < rows.size(); i++)
            {
                out.write(rows.get(i) + "\n");
                n++;
            }
            out.close();
        }
        catch (Exception e)
        {
            PokecubeMod.log(Level.SEVERE, name + " " + n, e);
        }
    }

    public static ArrayList<String> getFile(String file)
    {
        InputStream res = (PokecubeMobs.class).getResourceAsStream(file);

        ArrayList<String> rows = new ArrayList<String>();
        BufferedReader br = null;
        String line = "";
        try
        {

            br = new BufferedReader(new InputStreamReader(res));
            while ((line = br.readLine()) != null)
            {
                rows.add(line);
            }

        }
        catch (FileNotFoundException e)
        {
            PokecubeMod.log(Level.SEVERE, "Missing a Database file " + file, e);
        }
        catch (NullPointerException e)
        {
            try
            {
                FileReader temp = new FileReader(new File(file));
                br = new BufferedReader(temp);
                while ((line = br.readLine()) != null)
                {
                    rows.add(line);
                }
            }
            catch (Exception e1)
            {
                PokecubeMod.log(Level.SEVERE, "Error with " + file, e1);
            }

        }
        catch (Exception e)
        {
            PokecubeMod.log(Level.SEVERE, "Error with " + file, e);
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (Exception e)
                {
                    PokecubeMod.log(Level.SEVERE, "Error with " + file, e);
                }
            }
        }

        return rows;
    }
}
