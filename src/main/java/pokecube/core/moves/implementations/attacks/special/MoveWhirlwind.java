package pokecube.core.moves.implementations.attacks.special;

import net.minecraft.entity.EntityLiving;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.interfaces.pokemob.ai.GeneralStates;
import pokecube.core.moves.templates.Move_Basic;

public class MoveWhirlwind extends Move_Basic
{

    public MoveWhirlwind(String string)
    {
        super(string);
    }

    public MoveWhirlwind()
    {
        super("whirlwind");
    }

    @Override
    public void postAttack(MovePacket packet)
    {
        super.postAttack(packet);
        if (packet.canceled || packet.failed) return;
        IPokemob attacked = CapabilityPokemob.getPokemobFor(packet.attacked);
        if (attacked != null)
        {
            if (attacked.getLevel() > packet.attacker.getLevel())
            {
                // TODO message here for move failing;
                return;
            }
            if (attacked.getGeneralState(GeneralStates.TAMED)) attacked.returnToPokecube();
            attacked.setCombatState(CombatStates.ANGRY, false);
        }
        // ends the battle
        if (packet.attacked instanceof EntityLiving)
        {
            ((EntityLiving) packet.attacked).setAttackTarget(null);
        }
        packet.attacker.setCombatState(CombatStates.ANGRY, false);
        packet.attacker.getEntity().setAttackTarget(null);
    }
}
