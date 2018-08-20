package pokecube.core.database.abilities.p;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import pokecube.core.PokecubeItems;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.items.ItemTM;
import thut.lib.CompatWrapper;

public class Pickup extends Ability
{
    public static ResourceLocation lootTable;

    @Override
    public void onUpdate(IPokemob mob)
    {
        EntityLivingBase poke = mob.getEntity();
        if (poke.ticksExisted % 200 == 0 && Math.random() < 0.1)
        {
            if (!CompatWrapper.isValid(mob.getHeldItem()))
            {
                if (lootTable != null)
                {
                    LootTable loottable = mob.getEntity().getEntityWorld().getLootTableManager()
                            .getLootTableFromLocation(lootTable);
                    LootContext.Builder lootcontext$builder = (new LootContext.Builder(
                            (WorldServer) mob.getEntity().getEntityWorld())).withLootedEntity(mob.getEntity());
                    if (mob.getPokemonOwner() instanceof EntityPlayerMP)
                    {
                        lootcontext$builder.withPlayer((EntityPlayer) mob.getPokemonOwner());
                    }
                    for (ItemStack itemstack : loottable.generateLootForPools(mob.getEntity().getRNG(),
                            lootcontext$builder.build()))
                    {
                        if (CompatWrapper.isValid(itemstack))
                        {
                            mob.setHeldItem(itemstack.copy());
                        }
                    }
                }
                else
                {
                    List<?> items = new ArrayList<Object>(PokecubeItems.heldItems);
                    Collections.shuffle(items);
                    ItemStack item = (ItemStack) items.get(0);
                    //No TMs in pickup, as they are unmapped
                    if (CompatWrapper.isValid(item) && !(item.getItem() instanceof ItemTM))
                        mob.setHeldItem(item.copy());
                }
            }
        }
    }

}
