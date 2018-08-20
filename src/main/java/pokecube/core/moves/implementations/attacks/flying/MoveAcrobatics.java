package pokecube.core.moves.implementations.attacks.flying;

import net.minecraft.entity.Entity;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.moves.templates.Move_Basic;
import thut.lib.CompatWrapper;

public class MoveAcrobatics extends Move_Basic
{

    public MoveAcrobatics()
    {
        super("acrobatics");
    }

    @Override
    public int getPWR(IPokemob attacker, Entity attacked)
    {
        int bonus = 1;
        if (!CompatWrapper.isValid(attacker.getHeldItem())) bonus = 2;
        return getPWR() * bonus;
    }
}
