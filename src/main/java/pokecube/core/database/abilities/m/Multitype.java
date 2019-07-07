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
        ItemStack held = mob.getHeldItem();
        if (ItemBadge.isBadge(held))
        {
            ItemBadge badge = (ItemBadge) held.getItem();
            PokeType type = badge.type;
            PokedexEntry forme = Database.getEntry(entry.getBaseName() + type);
            if (forme != null)
            {
                mob.setPokedexEntry(forme);
                return;
            }
        }
        if (entry.getBaseForme() != null)
        {
            mob.setPokedexEntry(entry.getBaseForme());
            return;
        }
    }
}
