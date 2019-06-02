package pokecube.origin;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import pokecube.mobs.PokecubeMobs;
import pokecube.mobs.Reference;

@Mod(modid = PokecubeOrigin.MODID, name = "Pokecube Origin", version = Reference.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "*")
public class PokecubeOrigin
{
    public static final String MODID = "pokecube_origin";

    @EventHandler
    public void preInit(FMLPreInitializationEvent e)
    {
        doMetastuff();
    }

    private void doMetastuff()
    {
        ModMetadata meta = FMLCommonHandler.instance().findContainerFor(this).getMetadata();
        meta.parent = PokecubeMobs.MODID;
    }
}
