package pokecube.mobs;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import pokecube.core.database.PokedexEntry;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.modelloader.CommonProxy;
import pokecube.modelloader.IMobProvider;

@Mod(modid = PokecubeMissingno.MODID, name = "Pokecube MissingNos", version = Reference.VERSION, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "*")
public class PokecubeMissingno implements IMobProvider
{
    public static final String MODID       = "pokecube_mno";
    public static final String MODELPATH   = "entity/models/";
    public static final String TEXTUREPATH = PokedexEntry.TEXTUREPATH;

    public PokecubeMissingno()
    {
        CommonProxy.registerModelProvider(MODID, this);
    }

    /** This function is called by Forge at initialization.
     * 
     * @param evt */
    @EventHandler
    public void preInit(FMLCommonSetupEvent evt)
    {
        doMetastuff();
    }

    private void doMetastuff()
    {
        ModMetadata meta = FMLCommonHandler.instance().findContainerFor(this).getMetadata();
        meta.parent = PokecubeMod.ID;
    }

    @Override
    public String getModelDirectory(PokedexEntry entry)
    {
        return MODELPATH;
    }

    @Override
    public String getTextureDirectory(PokedexEntry entry)
    {
        return TEXTUREPATH;
    }

    @Override
    public Object getMod()
    {
        return this;
    }

}
