package pokecube.core.database.abilities.t;

import net.minecraft.entity.LivingEntity;
import pokecube.core.database.abilities.Ability;
import pokecube.core.database.abilities.AbilityManager;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.moves.MovePacket;

public class Trace extends Ability
{
    Ability traced;

    @Override
    public void onAgress(IPokemob mob, LivingEntity target)
    {
        IPokemob targetMob = CapabilityPokemob.getPokemobFor(target);
        if (traced != null) traced.onAgress(mob, target);
        else if (targetMob != null)
        {
            Ability ability = targetMob.getAbility();
            if (ability != null)
            {
                traced = AbilityManager.makeAbility(ability.getClass(), mob);
            }
        }
    }

    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        if (traced != null) traced.onMoveUse(mob, move);
    }

    @Override
    public void onUpdate(IPokemob mob)
    {
        if (traced != null && mob.getEntity().getAttackTarget() == null)
        {
            traced.destroy();
            traced = null;
        }
        else if (traced != null)
        {
            traced.onUpdate(mob);
        }
    }

}
