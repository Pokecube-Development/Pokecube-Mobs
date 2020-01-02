package pokecube.core.database.abilities.h;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.PokecubeMod;
import thut.api.maths.Vector3;

public class HoneyGather extends Ability
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
    public void onUpdate(IPokemob mob)
    {
        double diff = (0.001 * range * range);
        diff = Math.min(0.5, diff);
        EntityLivingBase entity = mob.getEntity();
        double randNum = Math.random();
        if (randNum < 1 - diff || entity.getEntityWorld().isRemote) return;

        Vector3 here = Vector3.getNewVector().set(entity);
        Random rand = entity.getRNG();

        here.set(entity).addTo(range * (rand.nextDouble() - 0.5), Math.min(10, range) * (rand.nextDouble() - 0.5),
                range * (rand.nextDouble() - 0.5));

        IBlockState state = here.getBlockState(entity.getEntityWorld());
        Block block = state.getBlock();
        if (block instanceof IGrowable)
        {
            IGrowable growable = (IGrowable) block;
            if (growable.canGrow(entity.getEntityWorld(), here.getPos(), here.getBlockState(entity.getEntityWorld()),
                    false))
            {
                if (PokecubeMod.debug) PokecubeMod.log("Honey Gather Bonemeal at: " + here.getPos());
                if (growable.canUseBonemeal(entity.getEntityWorld(), entity.getEntityWorld().rand, here.getPos(),
                        state))
                {
                    growable.grow(entity.getEntityWorld(), entity.getEntityWorld().rand, here.getPos(), state);
                    return;
                }
            }
        }
    }
}
