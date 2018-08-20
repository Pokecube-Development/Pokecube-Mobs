package pokecube.core.moves.implementations.attacks.special;

import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.moves.templates.Move_Basic;

public class MoveRest extends Move_Basic
{

    public MoveRest()
    {
        super("rest");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        super.postAttack(packet);
        if (packet.canceled || packet.failed) return;
        packet.attacker.healStatus();
        packet.attacker.healChanges();
        packet.attacker.setStatus(STATUS_SLP, 2);
    }
}
