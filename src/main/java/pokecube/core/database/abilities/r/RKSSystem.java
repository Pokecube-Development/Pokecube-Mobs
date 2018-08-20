package pokecube.core.database.abilities.r;

import net.minecraft.item.ItemStack;
import pokecube.adventures.items.ItemBadge;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.utils.PokeType;

public class RKSSystem extends Ability
{
    private static PokeType normal = null;

    @Override
    public void onUpdate(IPokemob mob)
    {
        PokedexEntry entry = mob.getPokedexEntry();
        if (normal == null) normal = PokeType.getType("normal");
        if (!entry.getName().contains("Silvally")) return;
        ItemStack held = mob.getHeldItem();
        if (ItemBadge.isBadge(held))
        {
            PokeType type = PokeType.values()[held.getItemDamage()];
            if (type != PokeType.unknown)
            {
                mob.setType1(type);
                return;
            }
        }
        if (normal != null) mob.setType1(normal);
    }
}
