package pokecube.core.database.abilities.s;

import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;

public class Schooling extends Ability
{
    private static PokedexEntry base;
    private static PokedexEntry school;
    private static boolean      noTurn = false;

    @Override
    public void onUpdate(IPokemob mob)
    {
        if (noTurn) return;
        if (base == null)
        {
            base = Database.getEntry("Wishiwashi");
            school = Database.getEntry("Wishiwashi School");
            noTurn = base == null || school == null;
            if (noTurn) return;
        }
        PokedexEntry mobs = mob.getPokedexEntry();
        if (!(mobs == base || mobs == school)) return;
        if (mob.getLevel() < 20)
        {
            if (mobs == school) mob.setPokedexEntry(base);
            return;
        }
        if (mob.getEntity().getHealth() > mob.getEntity().getMaxHealth() * 0.25)
        {
            if (mobs == base) mob.setPokedexEntry(school);
        }
        else
        {
            if (mobs == school) mob.setPokedexEntry(base);
        }
    }
}
