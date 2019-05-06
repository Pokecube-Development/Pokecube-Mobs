package pokecube.core.database.abilities.m;

import java.util.List;

import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.items.pokemobeggs.EntityPokemobEgg;
import thut.api.maths.Vector3;

public class MagmaArmor extends Ability
{

    int range = 4;

    @Override
    public Ability init(Object... args)
    {
        for (int i = 0; i < 2; i++)
            if (args != null && args.length > i)
            {
                if (args[i] instanceof Integer)
                {
                    range = (int) args[i];
                    return this;
                }
            }
        return this;
    }

    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {
        IPokemob attacker = move.attacker;
        if (attacker == mob || !move.pre || attacker == move.attacked) return;
        if (move.statusChange == IMoveConstants.STATUS_FRZ) move.statusChange = IMoveConstants.STATUS_NON;
    }

    @Override
    public void onUpdate(IPokemob mob)
    {
        if (mob.getStatus() == IMoveConstants.STATUS_FRZ) mob.healStatus();
        Vector3 v = Vector3.getNewVector().set(mob.getEntity());
        List<EntityPokemobEgg> eggs = mob.getEntity().getEntityWorld().getEntitiesWithinAABB(EntityPokemobEgg.class,
                v.getAABB().expand(range, range, range));
        for (EntityPokemobEgg egg : eggs)
        {
            egg.incubateEgg();
        }
    }

}
