package pokecube.core.moves.implementations.attacks.special;

import pokecube.core.PokecubeCore;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.templates.Move_Basic;

public class MoveDisable extends Move_Basic
{

    public MoveDisable()
    {
        super("disable");
    }

    @Override
    public void onAttack(MovePacket packet)
    {
        super.onAttack(packet);
        if (!(packet.canceled || packet.failed || packet.denied))
        {
            IPokemob target = CapabilityPokemob.getPokemobFor(packet.attacked);
            if (target != null)
            {
                int index = packet.attacker.getEntity().getRNG().nextInt(4);
                int timer = packet.attacker.getEntity().getRNG().nextInt(7);
                if (target.getDisableTimer(index) <= 0 && timer > 0)
                {
                    target.setDisableTimer(index, PokecubeCore.core.getConfig().attackCooldown * timer);
                }
                else
                {
                    // TODO failed message
                    return;
                }
            }
        }
    }
}