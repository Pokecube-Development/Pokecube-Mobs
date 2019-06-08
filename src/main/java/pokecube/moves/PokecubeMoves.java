package pokecube.moves;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import pokecube.core.PokecubeCore;
import pokecube.core.blocks.healtable.ContainerHealTable;
import pokecube.core.blocks.healtable.TileHealTable;
import pokecube.core.items.pokecubes.EntityPokecubeBase;
import pokecube.mobs.PokecubeMobs;
import pokecube.mobs.Reference;

@Mod(modid = PokecubeMoves.MODID, name = "Pokecube Movess", version = Reference.VERSION, dependencies = "required-after:pokecube", acceptableRemoteVersions = "*", acceptedMinecraftVersions = PokecubeMoves.MCVERSIONS)
public class PokecubeMoves
{
    public static final String MODID      = "pokecube_moves";
    public final static String MCVERSIONS = "*";

    public PokecubeMoves()
    {
    }

    private void doMetastuff()
    {
        ModMetadata meta = FMLCommonHandler.instance().findContainerFor(this).getMetadata();
        meta.parent = PokecubeMobs.MODID;
    }

    @EventHandler
    public void preInit(FMLCommonSetupEvent evt)
    {
        doMetastuff();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ResourceLocation sound = new ResourceLocation(MODID + ":pokecube_caught");
        PokecubeCore.instance.helper.registerSound(EntityPokecubeBase.POKECUBESOUND = new SoundEvent(sound), sound);
        sound = new ResourceLocation(MODID + ":pokecenter");
        PokecubeCore.instance.helper.registerSound(ContainerHealTable.HEAL_SOUND = new SoundEvent(sound), sound);
        sound = new ResourceLocation(MODID + ":pokecenterloop");
        PokecubeCore.instance.helper.registerSound(TileHealTable.MUSICLOOP = new SoundEvent(sound), sound);
    }

}
