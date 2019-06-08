package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.entity.LivingEntity;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.interfaces.pokemob.moves.MovePacket;
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
        LivingEntity attacker = packet.attacker.getEntity();
        if (!packet.attacker.getMoveStats().biding)
        {
            attacker.getEntityData().putLong("bideTime",
                    attacker.getEntityWorld().getGameTime() + PokecubeMod.core.getConfig().attackCooldown);
            packet.attacker.getMoveStats().biding = true;
            packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER = 0;
        }
        else
        {
            if (attacker.getEntityData().getLong("bideTime") < attacker.getEntityWorld().getGameTime())
            {
                attacker.getEntityData().remove("bideTime");
                int damage = 2 * packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER;
                packet.attacker.getMoveStats().PHYSICALDAMAGETAKENCOUNTER = 0;
                if (packet.attacked != null) packet.attacked.attackEntityFrom(
                        new PokemobDamageSource("mob", attacker, this), damage);
                packet.attacker.getMoveStats().biding = false;
            }
        }
    }
}
