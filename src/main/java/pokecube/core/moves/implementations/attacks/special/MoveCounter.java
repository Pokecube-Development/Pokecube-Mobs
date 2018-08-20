package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.entity.EntityLivingBase;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.moves.PokemobDamageSource;
import pokecube.core.moves.templates.Move_Basic;

public class MoveCounter extends Move_Basic
{

    public MoveCounter()
    {
        super("counter");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        super.postAttack(packet);
        if (packet.canceled || packet.failed) return;
        EntityLivingBase attacker = packet.attacker.getEntity();
        if (!packet.attacker.getMoveStats().biding)
        {
            attacker.getEntityData().setLong("bideTime",
                    attacker.getEntityWorld().getTotalWorldTime() + PokecubeMod.core.getConfig().attackCooldown);
            packet.attacker.getMoveStats().biding = true;
            packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER = 0;
        }
        else
        {
            if (attacker.getEntityData().getLong("bideTime") < attacker.getEntityWorld().getTotalWorldTime())
            {
                attacker.getEntityData().removeTag("bideTime");
                int damage = 2 * packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER;
                packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER = 0;
                if (packet.attacked != null) packet.attacked.attackEntityFrom(
                        new PokemobDamageSource("mob", attacker, this), damage);
                packet.attacker.getMoveStats().biding = false;
            }
        }
    }
}
