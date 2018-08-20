package pokecube.core.database.abilities.i;

import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IMoveConstants;
import pokecube.core.interfaces.IPokemob;

public class Immunity extends Ability
{
    @Override
    public void onUpdate(IPokemob mob)
    {
        if ((mob.getStatus() & IMoveConstants.STATUS_PSN) > 0) mob.healStatus();
    }
}
