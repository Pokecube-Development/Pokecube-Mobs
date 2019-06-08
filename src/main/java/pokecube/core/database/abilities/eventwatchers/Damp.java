package pokecube.core.database.abilities.eventwatchers;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.moves.MovePacket;
import pokecube.core.moves.templates.Move_Explode;
import thut.api.maths.Vector3;

public class Damp extends Ability
{
    IPokemob mob;
    int      range = 16;

    @SubscribeEvent
    public void denyBoom(ExplosionEvent.Start boom)
    {
        if (mob.getEntity().isDead) destroy();
        else
        {
            Vector3 boomLoc = Vector3.getNewVector().set(boom.getExplosion().getPosition());
            if (boomLoc.distToEntity(mob.getEntity()) < range)
            {
                boom.setCanceled(true);
            }
        }
    }

    @Override
    public void destroy()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Dist.CLIENT) return;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @Override
    public Ability init(Object... args)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Dist.CLIENT) return this;
        for (int i = 0; i < 2; i++)
            if (args != null && args.length > i)
            {
                if (IPokemob.class.isInstance(args[i]))
                {
                    MinecraftForge.EVENT_BUS.register(this);
                    mob = IPokemob.class.cast(args[i]);
                }
                if (args[i] instanceof Integer)
                {
                    range = (int) args[i];
                }
            }
        return this;
    }

    @Override
    public void onAgress(IPokemob mob, LivingEntity target)
    {
    }

    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        if (move.getMove() instanceof Move_Explode)
        {
            move.failed = true;
            move.canceled = true;
        }
    }

    @Override
    public void onUpdate(IPokemob mob)
    {
        this.mob = mob;
    }

}
