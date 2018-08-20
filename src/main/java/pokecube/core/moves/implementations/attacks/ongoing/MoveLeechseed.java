package pokecube.core.moves.implementations.attacks.ongoing;

import net.minecraft.entity.EntityLivingBase;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.entity.IOngoingAffected;
import pokecube.core.interfaces.entity.IOngoingAffected.IOngoingEffect;
import pokecube.core.moves.templates.Move_Ongoing;

public class MoveLeechseed extends Move_Ongoing
{

    public MoveLeechseed()
    {
        super("leechseed");
    }

    @Override
    public void doOngoingEffect(IOngoingAffected mob, IOngoingEffect effect)
    {
        EntityLivingBase living = mob.getEntity();
        IPokemob pokemob = CapabilityPokemob.getPokemobFor(living);
        float factor = 0.0625f;
        if (pokemob != null)
        {
            factor *= (pokemob.getMoveStats().TOXIC_COUNTER + 1);
        }
        float thisMaxHP = living.getMaxHealth();
        float damage = damageTarget(living, null, Math.max(1, (int) (factor * thisMaxHP)));
        EntityLivingBase target = living.getAttackingEntity();
        if (target == null) target = living.getRevengeTarget();
        if (target == null) target = living.getLastAttackedEntity();
        if (target != null) target.setHealth(Math.min(target.getHealth() + damage, target.getMaxHealth()));
    }

    @Override
    public int getDuration()
    {
        return -1;
    }

}
