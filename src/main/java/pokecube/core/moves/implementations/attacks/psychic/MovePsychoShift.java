package pokecube.core.moves.implementations.attacks.psychic;

import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.templates.Move_Basic;

public class MovePsychoShift extends Move_Basic
{

    public MovePsychoShift()
    {
        super("psychoshift");
    }

    @Override
    public void onAttack(MovePacket packet)
    {
        super.onAttack(packet);
        if (!(packet.canceled || packet.failed || packet.denied))
        {
            if (packet.attacker.getStatus() == IPokemob.STATUS_NON)
            {
                // TODO send failed message.
                return;
            }
            IPokemob hit = CapabilityPokemob.getPokemobFor(packet.attacked);
            if (hit != null)
            {
                if (hit.getStatus() != IPokemob.STATUS_NON)
                {
                    // TODO send failed message.
                    return;
                }
                if (hit.setStatus(packet.attacker.getStatus()))
                {
                    packet.attacker.healStatus();
                }
                else
                {
                    // TODO send failed message.
                    return;
                }
            }
        }
    }

}