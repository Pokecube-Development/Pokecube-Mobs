package pokecube.core.database.abilities.s;

import java.util.Random;

import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;

public class ShieldsDown extends Ability
{
    private static final PokedexEntry[] cores  = new PokedexEntry[7];
    private static PokedexEntry         base;
    private static boolean              noBase = false;

    private static void initFormes()
    {
        base = Database.getEntry(774);
        if (base == null)
        {
            noBase = true;
            return;
        }
        cores[0] = Database.getEntry("Blue Core Minior");
        cores[1] = Database.getEntry("Green Core Minior");
        cores[2] = Database.getEntry("Indigo Core Minior");
        cores[3] = Database.getEntry("Orange Core Minior");
        cores[4] = Database.getEntry("Red Core Minior");
        cores[5] = Database.getEntry("Violet Core Minior");
        cores[6] = Database.getEntry("Yellow Core Minior");
        for (int i = 0; i < 7; i++)
        {
            if (cores[i] == null) cores[i] = base;
        }
    }

    @Override
    public void onUpdate(IPokemob mob)
    {
        if (noBase) return;
        if (base == null)
        {
            initFormes();
        }
        PokedexEntry entry = mob.getPokedexEntry();
        if (base == null || entry.getPokedexNb() != base.getPokedexNb()) return;
        float ratio = mob.getEntity().getHealth() / mob.getEntity().getMaxHealth();
        if (ratio < 0.5)
        {
            PokedexEntry core = getCoreEntry(mob);
            if (core != null && core != entry)
            {
                mob.setPokedexEntry(core);
            }
        }
        else if (entry != base)
        {
            mob.setPokedexEntry(base);
        }
    }

    private PokedexEntry getCoreEntry(IPokemob mob)
    {
        int num = mob.getRNGValue();
        Random rand = new Random(num);
        int index = rand.nextInt(cores.length);
        return cores[index];
    }
}
