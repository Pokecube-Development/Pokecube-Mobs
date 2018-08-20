package pokecube.core.database.abilities.m;

import net.minecraft.item.ItemStack;
import pokecube.adventures.items.ItemBadge;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.PokeType;

public class Multitype extends Ability
{
    @Override
    public void onUpdate(IPokemob mob)
    {
        PokedexEntry entry = mob.getPokedexEntry();
        if (!entry.getName().contains("Arceus")) return;
        ItemStack held = mob.getHeldItem();
        if (ItemBadge.isBadge(held))
        {
            PokeType type = PokeType.values()[held.getItemDamage()];
            if (type != PokeType.unknown)
            {
                mob.setPokedexEntry(Database.getEntry("arceus" + type));
                return;
            }
        }
        if (entry.getBaseForme() != null && entry.getBaseName().equals("Arceus"))
        {
            mob.setPokedexEntry(entry.getBaseForme());
            return;
        }

    }
}
