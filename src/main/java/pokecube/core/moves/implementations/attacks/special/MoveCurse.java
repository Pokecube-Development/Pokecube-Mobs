package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.util.DamageSource;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.MovesUtils;
import pokecube.core.moves.templates.Move_Basic;
import pokecube.core.utils.PokeType;

public class MoveCurse extends Move_Basic
{

    public MoveCurse()
    {
        super("curse");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        super.postAttack(packet);
        if (packet.canceled || packet.failed) return;
        if (packet.attacker.isType(PokeType.getType("ghost")))
        {
            IPokemob target = CapabilityPokemob.getPokemobFor(packet.attacked);
            boolean apply = true;
            if (target != null)
            {
                apply = false;
                if ((target.getChanges() & CHANGE_CURSE) == 0)
                {
                    apply = true;
                }
            }
            if (apply)
            {
                MovePacket move = new MovePacket(packet.attacker, packet.attacked, getName(), PokeType.getType("ghost"),
                        0, 0, (byte) 0, CHANGE_CURSE, true);
                if (target != null) target.onMoveUse(move);
                if (!move.canceled)
                {
                    MovesUtils.addChange(packet.attacked, packet.attacker, CHANGE_CURSE);
                    packet.attacker.getEntity().attackEntityFrom(DamageSource.MAGIC,
                            packet.attacker.getEntity().getMaxHealth() / 2);
                }
            }
        }
        else if (packet.attacked != packet.attacker && packet.attacked != null)
        {
            packet = new MovePacket(packet.attacker, packet.attacked, this);
            MovesUtils.handleStats(packet.attacker, packet.attacked, packet, true);
        }
    }
}
