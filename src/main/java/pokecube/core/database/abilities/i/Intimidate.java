package pokecube.core.database.abilities.i;

import net.minecraft.entity.EntityLivingBase;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.moves.MovesUtils;

public class Intimidate extends Ability
{

    @Override
    public void onAgress(IPokemob mob, EntityLivingBase target)
    {
        IPokemob targetMob = CapabilityPokemob.getPokemobFor(target);
        if (targetMob != null)
        {
            MovesUtils.handleStats2(targetMob, mob.getOwner(), IMoveConstants.ATTACK, IMoveConstants.FALL);
        }
    }
}
