package pokecube.core.database.abilities.a;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ExplosionEvent;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.Move_Base;
import pokecube.core.interfaces.pokemob.moves.MovePacket;

public class Aftermath extends Ability
{
    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        if (mob != move.attacked || move.pre || move.attacker == move.attacked) return;
        Move_Base attack = move.getMove();
        if (attack == null || (attack.getAttackCategory() & IMoveConstants.CATEGORY_CONTACT) == 0) return;

        if (mob.getEntity().getHealth() <= 0)
        {
            Explosion boom = new Explosion(move.attacked.getEntityWorld(), move.attacked, move.attacked.posX,
                    move.attacked.posY, move.attacked.posZ, 0, false, false);
            ExplosionEvent evt = new ExplosionEvent.Start(move.attacked.getEntityWorld(), boom);
            MinecraftForge.EVENT_BUS.post(evt);
            if (!evt.isCanceled())
            {
                EntityLivingBase attacker = move.attacker.getEntity();
                float hp = attacker.getHealth();
                attacker.attackEntityFrom(DamageSource.MAGIC, hp / 4);
            }
        }
    }
}
