package pokecube.mobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.handlers.ItemGenerator;
import pokecube.core.interfaces.IPokecube;
import pokecube.core.items.berries.BerryManager;
import pokecube.core.items.megastuff.ItemMegawearable;
import pokecube.core.items.vitamins.ItemVitamin;
import pokecube.core.utils.PokeType;;

public class CommandGenStuff extends CommandBase
{

    @Override
    public String getName()
    {
        return "pokemobsfiles";
    }

    @Override
    public String getUsage(ICommandSource sender)
    {
        return "/pokemobsfiles";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSource sender, String[] args) throws CommandException
    {
        sender.sendMessage(new StringTextComponent("Starting File Output"));
        for (PokedexEntry e : Database.getSortedFormes())
        {
            if (e == Database.missingno) continue;
            registerAchievements(e);
        }
        sender.sendMessage(new StringTextComponent("Advancements Done"));
        File dir = new File("./mods/pokecube/assets/pokecube_mobs/");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "sounds.json");
        boolean small = false;
        for (String s : args)
        {
            if (s.startsWith("s")) small = true;
        }
        String json = SoundJsonGenerator.generateSoundJson(small);
        try
        {
            FileWriter write = new FileWriter(file);
            write.write(json);
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        sender.sendMessage(new StringTextComponent("Sounds Done"));
        generateBlockAndItemJsons();

        for (String s : ItemGenerator.barks.keySet())
        {
            // BlockStates
            // Bark
            dir = new File("./mods/pokecube_mobs/assets/pokecube/blockstates/");
            dir.mkdirs();
            file = new File(dir, "bark_" + s + ".json");
            json = "{\n" + "    \"variants\": {\n" + "        \"normal\": { \"model\": \"pokecube:bark_" + s + "\" }\n"
                    + "    }\n" + "}";
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            // Bark
            file = new File(dir, "plank_" + s + ".json");
            json = "{\n" + "    \"variants\": {\n" + "        \"normal\": { \"model\": \"pokecube:plank_" + s + "\" }\n"
                    + "    }\n" + "}";
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            // Log
            json = "{\n" + "    \"variants\": {\n" + "        \"axis=y\": { \"model\": \"pokecube:log_" + s + "\" },\n"
                    + "        \"axis=z\":  { \"model\": \"pokecube:log_" + s + "\",\"x\": 90 },\n"
                    + "        \"axis=x\": { \"model\": \"pokecube:log_" + s + "\", \"x\": 90, \"y\": 90 },\n"
                    + "        \"axis=none\": { \"model\": \"pokecube:bark_" + s + "\"}\n" + "    }\n" + "}";
            file = new File(dir, "log_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Block models

            dir = new File("./mods/pokecube_mobs/assets/pokecube/models/block/");
            dir.mkdirs();

            json = "{\n" + "   \"parent\": \"block/cube_all\",\n" + "   \"textures\": {\n"
                    + "       \"all\": \"pokecube:blocks/bark_" + s + "\"\n" + "   }\n" + "}\n";
            file = new File(dir, "bark_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            json = "{\n" + "   \"parent\": \"block/cube_all\",\n" + "   \"textures\": {\n"
                    + "       \"all\": \"pokecube:blocks/plank_" + s + "\"\n" + "   }\n" + "}";
            file = new File(dir, "plank_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            json = "{\n" + "   \"parent\": \"block/cube_column\",\n" + "   \"textures\": {\n"
                    + "       \"end\": \"pokecube:blocks/log_" + s + "\",\n"
                    + "       \"side\": \"pokecube:blocks/bark_" + s + "\"\n" + "   }\n" + "}";
            file = new File(dir, "log_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Item Jsons
            dir = new File("./mods/pokecube_mobs/assets/pokecube/models/item/");
            dir.mkdirs();

            json = "{\n" + "   \"parent\": \"pokecube:block/plank_" + s + "\",\n" + "   \"display\": {\n"
                    + "       \"thirdperson\": {\n" + "           \"rotation\": [ 10, -45, 170 ],\n"
                    + "           \"translation\": [ 0, 1.5, -2.75 ],\n"
                    + "           \"scale\": [ 0.375, 0.375, 0.375 ]\n" + "       }\n" + "   }\n" + "}";
            file = new File(dir, "plank_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            json = "{\n" + "   \"parent\": \"pokecube:block/bark_" + s + "\",\n" + "   \"display\": {\n"
                    + "       \"thirdperson\": {\n" + "           \"rotation\": [ 10, -45, 170 ],\n"
                    + "           \"translation\": [ 0, 1.5, -2.75 ],\n"
                    + "           \"scale\": [ 0.375, 0.375, 0.375 ]\n" + "       }\n" + "   }\n" + "}";
            file = new File(dir, "bark_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            json = "{\n" + "   \"parent\": \"pokecube:block/log_" + s + "\",\n" + "   \"display\": {\n"
                    + "       \"thirdperson\": {\n" + "           \"rotation\": [ 10, -45, 170 ],\n"
                    + "           \"translation\": [ 0, 1.5, -2.75 ],\n"
                    + "           \"scale\": [ 0.375, 0.375, 0.375 ]\n" + "       }\n" + "   }\n" + "}";
            file = new File(dir, "log_" + s + ".json");
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        sender.sendMessage(new StringTextComponent("Finished File Output"));
    }

    /** Comment these out to re-generate advancements. */
    public static void registerAchievements(PokedexEntry entry)
    {
        if (!entry.base) return;
        make(entry, "catch", "pokecube_mobs:capture/get_first_pokemob", "capture");
        make(entry, "kill", "pokecube_mobs:kill/root", "kill");
        make(entry, "hatch", "pokecube_mobs:hatch/root", "hatch");
    }

    protected static void make(PokedexEntry entry, String id, String parent, String path)
    {
        ResourceLocation key = new ResourceLocation(entry.getModId(), id + "_" + entry.getTrimmedName());
        String json = AdvancementGenerator.makeJson(entry, id, parent);
        File dir = new File("./mods/pokecube/assets/pokecube_mobs/advancements/" + path + "/");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, key.getResourcePath() + ".json");
        FileWriter write;
        try
        {
            write = new FileWriter(file);
            write.write(json);
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (id.equals("catch"))
        {
            File first = new File(dir, "get_first_pokemob.json");
            if (!first.exists())
            {
                JsonObject rootObj = new JsonObject();
                JsonObject displayJson = new JsonObject();
                JsonObject icon = new JsonObject();
                icon.addProperty("item", "pokecube:pokecube");
                JsonObject title = new JsonObject();
                title.addProperty("translate", "achievement.pokecube.get1st");
                JsonObject description = new JsonObject();
                description.addProperty("translate", "achievement.pokecube.get1st.desc");
                displayJson.add("icon", icon);
                displayJson.add("title", title);
                displayJson.add("description", description);
                JsonObject critmap = new JsonObject();
                JsonObject sub = new JsonObject();
                sub.addProperty("trigger", "pokecube:get_first_pokemob");
                critmap.add("get_first_pokemob", sub);
                rootObj.add("display", displayJson);
                rootObj.addProperty("parent", "pokecube_mobs:capture/root");
                rootObj.add("criteria", critmap);
                json = AdvancementGenerator.GSON.toJson(rootObj);
                try
                {
                    write = new FileWriter(first);
                    write.write(json);
                    write.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        File root = new File(dir, "root.json");
        if (!root.exists())
        {
            JsonObject rootObj = new JsonObject();
            JsonObject displayJson = new JsonObject();
            JsonObject icon = new JsonObject();
            icon.addProperty("item", "pokecube:pokecube");
            JsonObject title = new JsonObject();
            title.addProperty("translate", "achievement.pokecube." + id + ".root");
            JsonObject description = new JsonObject();
            description.addProperty("translate", "achievement.pokecube." + id + ".root.desc");
            displayJson.add("icon", icon);
            displayJson.add("title", title);
            displayJson.add("description", description);
            displayJson.addProperty("background", "minecraft:textures/gui/advancements/backgrounds/adventure.png");
            displayJson.addProperty("show_toast", false);
            displayJson.addProperty("announce_to_chat", false);
            JsonObject critmap = new JsonObject();
            JsonObject sub = new JsonObject();
            sub.addProperty("trigger", "pokecube:get_first_pokemob");
            critmap.add("get_first_pokemob", sub);
            rootObj.add("display", displayJson);
            rootObj.add("criteria", critmap);
            json = AdvancementGenerator.GSON.toJson(rootObj);
            try
            {
                write = new FileWriter(root);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void generateItemJson(String name, String prefix, String outerdir, String innerdir)
    {
        if (name.equals("???")) name = "unknown";
        JsonObject blockJson = new JsonObject();
        blockJson.addProperty("parent", "item/generated");
        JsonObject textures = new JsonObject();

        Map<String, String> meganames = Maps.newHashMap();
        meganames.put("aerodactylmega", "pokecube:items/aerodactylite");
        meganames.put("abomasnowmega", "pokecube:items/abomasite");
        meganames.put("absolmega", "pokecube:items/absolite");
        meganames.put("aggronmega", "pokecube:items/aggronite");
        meganames.put("alakazammega", "pokecube:items/alakazite");
        meganames.put("altariamega", "pokecube:items/altarianite");
        meganames.put("ampharosmega", "pokecube:items/ampharosite");
        meganames.put("audinomega", "pokecube:items/audinite");
        meganames.put("banettemega", "pokecube:items/banettite");
        meganames.put("beedrillmega", "pokecube:items/beedrillite");
        meganames.put("blastoisemega", "pokecube:items/blastoisinite");
        meganames.put("blazikenmega", "pokecube:items/blazikenite");
        meganames.put("cameruptmega", "pokecube:items/cameruptite");
        meganames.put("charizardmega-x", "pokecube:items/charizardite_x");
        meganames.put("charizardmega-y", "pokecube:items/charizardite_y");
        meganames.put("dianciemega", "pokecube:items/diancite");
        meganames.put("gallademega", "pokecube:items/galladite");
        meganames.put("garchompmega", "pokecube:items/garchompite");
        meganames.put("gardevoirmega", "pokecube:items/gardevoirite");
        meganames.put("gengarmega", "pokecube:items/gengarite");
        meganames.put("glaliemega", "pokecube:items/glalitite");
        meganames.put("gyaradosmega", "pokecube:items/gyaradosite");
        meganames.put("heracrossmega", "pokecube:items/heracronite");
        meganames.put("houndoommega", "pokecube:items/houndoominite");
        meganames.put("kangaskhanmega", "pokecube:items/kangaskhanite");
        meganames.put("latiasmega", "pokecube:items/latiasite");
        meganames.put("latiosmega", "pokecube:items/latiosite");
        meganames.put("lopunnymega", "pokecube:items/lopunnite");
        meganames.put("lucariomega", "pokecube:items/lucarionite");
        meganames.put("manectricmega", "pokecube:items/manectite");
        meganames.put("mawilemega", "pokecube:items/mawilite");
        meganames.put("medichammega", "pokecube:items/medichamite");
        meganames.put("metagrossmega", "pokecube:items/metagrossite");
        meganames.put("mewtwomega-x", "pokecube:items/mewtwonite_x");
        meganames.put("mewtwomega-y", "pokecube:items/mewtwonite_y");
        meganames.put("pidgeotmega", "pokecube:items/pidgeotite");
        meganames.put("pinsirmega", "pokecube:items/pinsirite");
        meganames.put("sableyemega", "pokecube:items/sablenite");
        meganames.put("salamencemega", "pokecube:items/salamencite");
        meganames.put("sceptilemega", "pokecube:items/sceptilite");
        meganames.put("scizormega", "pokecube:items/scizorite");
        meganames.put("sharpedomega", "pokecube:items/sharpedonite");
        meganames.put("slowbromega", "pokecube:items/slowbronite");
        meganames.put("steelixmega", "pokecube:items/steelixite");
        meganames.put("swampertmega", "pokecube:items/swampertite");
        meganames.put("tyranitarmega", "pokecube:items/tyranitarite");
        meganames.put("venusaurmega", "pokecube:items/venusaurite");

        String tex = innerdir + ":items/" + prefix + name;
        if (meganames.containsKey(name)) tex = meganames.get(name);

        textures.addProperty("layer0", tex);
        blockJson.add("textures", textures);
        File dir = new File("./mods/" + outerdir + "/assets/" + innerdir + "/models/item/");
        dir.mkdirs();
        File file = new File(dir, prefix + name + ".json");
        String json = AdvancementGenerator.GSON.toJson(blockJson);
        try
        {
            FileWriter write = new FileWriter(file);
            write.write(json);
            write.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void generateBlockAndItemJsons()
    {
        boolean berries = true;
        boolean cubes = false;
        boolean vitamins = true;
        boolean badges = true;
        boolean megastones = true;
        boolean megawearables = true;
        boolean fossils = true;

        if (badges) for (PokeType type : PokeType.values())
        {
            generateItemJson(type.name, "badge_", "pokecube_adventures", "pokecube_adventures");
        }
        if (fossils) for (String type : ItemGenerator.fossilVariants)
        {
            generateItemJson(type, "fossil_", "pokecube_mobs", "pokecube");
        }
        if (megastones) for (String type : ItemGenerator.variants)
        {
            generateItemJson(type, "", "pokecube_mobs", "pokecube");
        }
        if (vitamins) for (String type : ItemVitamin.vitamins)
        {
            generateItemJson(type, "vitamin_", "pokecube_mobs", "pokecube");
        }
        if (megawearables) for (String type : ItemMegawearable.getWearables())
        {
            String dir = type.equals("ring") || type.equals("hat") || type.equals("belt") ? "pokecube"
                    : "pokecube_mobs";
            generateItemJson(type, "mega_", dir, "pokecube");
        }

        if (berries) for (String name : BerryManager.berryNames.values())
        {
            String dir = name.equals("null") ? "pokecube" : "pokecube_mobs";
            generateItemJson(name, "berry_", dir, "pokecube");
        }

        if (cubes) for (ResourceLocation l : IPokecube.BEHAVIORS.getKeys())
        {
            String cube = l.getResourcePath();
            JsonObject blockJson = new JsonObject();
            blockJson.addProperty("parent", "pokecube:block/pokecubes");
            JsonObject textures = new JsonObject();
            textures.addProperty("top", "pokecube:items/" + cube + "cube" + "top");
            textures.addProperty("bottom", "pokecube:items/" + cube + "cube" + "bottom");
            textures.addProperty("front", "pokecube:items/" + cube + "cube" + "front");
            textures.addProperty("side", "pokecube:items/" + cube + "cube" + "side");
            textures.addProperty("back", "pokecube:items/" + cube + "cube" + "back");
            blockJson.add("textures", textures);

            File dir = new File("./mods/pokecube/assets/pokecube/models/block/");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, cube + "cube" + ".json");
            String json = AdvancementGenerator.GSON.toJson(blockJson);
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("parent", "pokecube:block/" + cube + "cube");
            JsonObject display = new JsonObject();
            JsonObject thirdPerson = new JsonObject();
            JsonArray rotation = new JsonArray();
            JsonArray translation = new JsonArray();
            JsonArray scale = new JsonArray();

            rotation.add(new JsonPrimitive(10));
            rotation.add(new JsonPrimitive(-45));
            rotation.add(new JsonPrimitive(170));

            translation.add(new JsonPrimitive(0));
            translation.add(new JsonPrimitive(1.5));
            translation.add(new JsonPrimitive(-2.75));

            scale.add(new JsonPrimitive(0.375));
            scale.add(new JsonPrimitive(0.375));
            scale.add(new JsonPrimitive(0.375));

            thirdPerson.add("rotation", rotation);
            thirdPerson.add("translation", translation);
            thirdPerson.add("scale", scale);
            display.add("thirdperson", thirdPerson);
            itemJson.add("display", display);

            dir = new File("./mods/pokecube/assets/pokecube/models/item/");
            if (!dir.exists()) dir.mkdirs();
            file = new File(dir, cube + "cube" + ".json");
            json = AdvancementGenerator.GSON.toJson(itemJson);
            try
            {
                FileWriter write = new FileWriter(file);
                write.write(json);
                write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    public static class SoundJsonGenerator
    {
        public static String generateSoundJson(boolean small)
        {
            JsonObject soundJson = new JsonObject();
            List<PokedexEntry> pokedexEntries = Database.getSortedFormes();
            Set<ResourceLocation> added = Sets.newHashSet();
            int num = small ? 1 : 3;
            for (PokedexEntry entry : pokedexEntries)
            {
                ResourceLocation event = entry.getSoundEvent().getSoundName();
                if (added.contains(event)) continue;
                added.add(event);

                String backup = "rattata";

                ResourceLocation test = new ResourceLocation(event.getResourceDomain() + ":"
                        + event.getResourcePath().replaceFirst("mobs.", "sounds/mobs/") + ".ogg");
                try
                {
                    Minecraft.getInstance().getResourceManager().getResource(test);
                }
                catch (Exception e)
                {
                    event = new ResourceLocation(backup);
                    System.out.println(entry + "->" + backup + " " + test);
                }

                String soundName = event.getResourcePath().replaceFirst("mobs.", "");
                JsonObject soundEntry = new JsonObject();
                soundEntry.addProperty("category", "hostile");
                soundEntry.addProperty("subtitle", entry.getUnlocalizedName());
                JsonArray sounds = new JsonArray();

                for (int i = 0; i < num; i++)
                {
                    JsonObject sound = new JsonObject();
                    sound.addProperty("name", "pokecube_mobs:mobs/" + soundName);
                    if (!small)
                    {
                        sound.addProperty("volume", (i == 0 ? 0.8 : i == 1 ? 0.9 : 1));
                        sound.addProperty("pitch", (i == 0 ? 0.9 : i == 1 ? 0.95 : 1));
                    }
                    sounds.add(sound);
                }
                soundEntry.add("sounds", sounds);
                soundJson.add("mobs." + entry.getTrimmedName(), soundEntry);
            }
            return AdvancementGenerator.GSON.toJson(soundJson);
        }
    }

    public static class AdvancementGenerator
    {
        static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

        public static JsonObject fromInfo(PokedexEntry entry, String id)
        {
            JsonObject displayJson = new JsonObject();
            JsonObject icon = new JsonObject();
            icon.addProperty("item", "pokecube:pokecube");
            JsonObject title = new JsonObject();
            title.addProperty("translate", "achievement.pokecube." + id);
            JsonArray item = new JsonArray();
            JsonObject pokemobName = new JsonObject();
            pokemobName.addProperty("translate", entry.getUnlocalizedName());
            item.add(pokemobName);
            title.add("with", item);
            JsonObject description = new JsonObject();
            description.addProperty("translate", "achievement.pokecube." + id + ".desc");
            description.add("with", item);
            displayJson.add("icon", icon);
            displayJson.add("title", title);
            displayJson.add("description", description);
            if (entry.legendary) displayJson.addProperty("frame", "challenge");
            return displayJson;
        }

        public static String[][] makeRequirements(PokedexEntry entry)
        {
            return new String[][] { { entry.getTrimmedName() } };
        }

        public static JsonObject fromCriteria(PokedexEntry entry, String id)
        {
            JsonObject critmap = new JsonObject();
            JsonObject sub = new JsonObject();
            sub.addProperty("trigger", "pokecube:" + id);
            JsonObject conditions = new JsonObject();
            if (id.equals("catch") || id.equals("kill")) conditions.addProperty("lenient", true);
            conditions.addProperty("entry", entry.getTrimmedName());
            sub.add("conditions", conditions);
            critmap.add(id + "_" + entry.getTrimmedName(), sub);
            return critmap;
        }

        public static String makeJson(PokedexEntry entry, String id, String parent)
        {
            JsonObject json = new JsonObject();
            json.add("display", fromInfo(entry, id));
            json.add("criteria", fromCriteria(entry, id));
            if (parent != null)
            {
                if (entry.evolvesFrom != null)
                {
                    String newParent = id + "_" + entry.evolvesFrom.getTrimmedName();
                    parent = parent.replace("root", newParent);
                    parent = parent.replace("get_first_pokemob", newParent);

                }
                json.addProperty("parent", parent);
            }
            return GSON.toJson(json);
        }
    }
}
