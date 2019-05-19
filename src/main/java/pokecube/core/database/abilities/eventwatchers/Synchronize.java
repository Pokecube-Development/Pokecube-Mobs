package pokecube.core.database.abilities.eventwatchers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import pokecube.core.database.abilities.Ability;
import pokecube.core.events.SpawnEvent;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.moves.MovePacket;
import pokecube.core.moves.MovesUtils;
import thut.api.maths.Vector3;

public class Synchronize extends Ability
{
    Vector3  location = Vector3.getNewVector();
    IPokemob pokemob;
    int      range    = 16;

    @Override
    public void destroy()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void editNature(SpawnEvent.Post event)
    {
        if (pokemob.getEntity().isDead) destroy();
        else if (event.location.distToSq(location) < range * range && Math.random() > 0.5)
        {
            event.pokemob.setNature(pokemob.getNature());
        }
    }

    @Override
    public Ability init(Object... args)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) return this;
        for (int i = 0; i < 1; i++)
            if (args != null && args.length > i)
            {
                if (IPokemob.class.isInstance(args[i]))
                {
                    MinecraftForge.EVENT_BUS.register(this);
                    location = Vector3.getNewVector().set(args[i]);
                    pokemob = IPokemob.class.cast(args[i]);
                }
                if (args[i] instanceof Integer)
                {
                    range = (int) args[i];
                }
            }
        return this;
    }

    @Override
    public void onAgress(IPokemob mob, EntityLivingBase target)
    {
    }

    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        if (mob == move.attacked && move.statusChange != IMoveConstants.STATUS_NON
                && mob.getStatus() == IMoveConstants.STATUS_NON)
        {
            if (move.statusChange != IMoveConstants.STATUS_FRZ && move.statusChange != IMoveConstants.STATUS_SLP)
                MovesUtils.setStatus(move.attacker.getEntity(), move.statusChange);
        }
    }

    @Override
    public void onUpdate(IPokemob mob)
    {
        location.set(mob.getEntity());
        pokemob = mob;
    }
}
