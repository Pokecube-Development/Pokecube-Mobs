package pokecube.core.database.abilities.p;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
import pokecube.core.interfaces.pokemob.ai.GeneralStates;
import pokecube.core.interfaces.pokemob.ai.LogicStates;
import pokecube.core.items.ItemTM;
import thut.lib.CompatWrapper;

public class Pickup extends Ability
{
    public static ResourceLocation lootTable    = new ResourceLocation("pokecube", "abilities/pickup");
    public static boolean          useLootTable = true;

    @Override
    public void onUpdate(IPokemob mob)
    {
        EntityLivingBase poke = mob.getEntity();
        // Staying in one place, nothing to find.
        if (mob.getGeneralState(GeneralStates.STAYING)) return;
        // Only works if your pokemob is following you.
        if (mob.getLogicState(LogicStates.SITTING)) return;

        if (poke.ticksExisted % 200 == 0 && Math.random() < 0.05)
        {
            if (!CompatWrapper.isValid(mob.getHeldItem()))
            {
                if (lootTable != null && useLootTable)
                {
                    // Load in the loot table.
                    LootTable loottable = mob.getEntity().getEntityWorld().getLootTableManager()
                            .getLootTableFromLocation(lootTable);
                    LootContext.Builder lootcontext$builder = (new LootContext.Builder(
                            (WorldServer) mob.getEntity().getEntityWorld())).withLootedEntity(mob.getEntity());
                    // Apply bonuses from the player
                    if (mob.getPokemonOwner() instanceof EntityPlayerMP)
                    {
                        lootcontext$builder.withPlayer((EntityPlayer) mob.getPokemonOwner());
                    }
                    // Generate the loot list.
                    List<ItemStack> list = loottable.generateLootForPools(new Random(), lootcontext$builder.build());
                    // Shuffle the list.
                    if (!list.isEmpty()) Collections.shuffle(list);
                    for (ItemStack itemstack : list)
                    {
                        // Pick first valid item in it.
                        if (CompatWrapper.isValid(itemstack))
                        {
                            ItemStack stack = itemstack.copy();
                            if (stack.getItem().getRegistryName().equals(new ResourceLocation("pokecube", "candy")))
                                PokecubeItems.makeStackValid(stack);
                            mob.setHeldItem(stack);
                            return;
                        }
                    }
                }
                else
                {
                    List<?> items = new ArrayList<Object>(PokecubeItems.heldItems);
                    Collections.shuffle(items);
                    ItemStack item = (ItemStack) items.get(0);
                    // No TMs in pickup, as they are unmapped
                    if (CompatWrapper.isValid(item) && !(item.getItem() instanceof ItemTM))
                        mob.setHeldItem(item.copy());
                }
            }
        }
    }

}
