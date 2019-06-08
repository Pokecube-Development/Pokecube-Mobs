package pokecube.mobs;

import java.util.List;

import javax.xml.namespace.QName;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.math.AxisAlignedBB;
import pokecube.core.PokecubeItems;
import pokecube.core.database.PokedexEntryLoader.SpawnRule;
import pokecube.core.database.SpawnBiomeMatcher;
import pokecube.core.database.SpawnBiomeMatcher.SpawnCheck;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.HappinessType;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import pokecube.core.interfaces.pokemob.ai.CombatStates;
import pokecube.core.interfaces.pokemob.ai.GeneralStates;
import pokecube.core.utils.PokeType;
import thut.api.maths.Vector3;

public class PokecubeHelper
{
    private static SpawnBiomeMatcher moonMatcher;

    static
    {
        SpawnRule rule = new SpawnRule();
        rule.values.put(new QName("rate"), "1");
        rule.values.put(new QName("types"), "moon");
        moonMatcher = new SpawnBiomeMatcher(rule);
    }

    public double dive(IPokemob mob)
    {
        double x = 1;
        Entity entity = mob.getEntity();
        if (entity.getEntityWorld().getBlockState(entity.getPosition()).getBlock() == Blocks.WATER
                && mob.isType(PokeType.getType("water")))
        {
            x = 3.5;
        }
        return x;
    }

    public double dusk(IPokemob mob)
    {
        double x = 1;
        Entity entity = mob.getEntity();
        int light = entity.getEntityWorld().getLight(entity.getPosition());
        if (light < 5)
        {
            x = 3.5;
        }
        return x;
    }

    public double nest(IPokemob mob)
    {
        double x = 1;
        if (mob.getLevel() < 20)
        {
            x = 3;
        }
        if (mob.getLevel() > 19 && mob.getLevel() < 30)
        {
            x = 2;
        }
        return x;
    }

    public double net(IPokemob mob)
    {
        double x = 1;
        if (mob.getType1() == PokeType.getType("bug"))
        {
            x = 2;
        }
        if (mob.getType1() == PokeType.getType("water"))
        {
            x = 2;
        }
        if (mob.getType2() == PokeType.getType("bug"))
        {
            x = 2;
        }
        if (mob.getType2() == PokeType.getType("water"))
        {
            x = 2;
        }
        return x;
    }

    public double quick(IPokemob mob)
    {
        double x = 1;
        Entity entity = mob.getEntity();
        double alive = entity.ticksExisted;
        if (!mob.getCombatState(CombatStates.ANGRY) && alive < 601)
        {
            x = 4;
        }
        return x;
    }

    public double premier(IPokemob mob)
    {
        double x = 0.25;
        if (!mob.getCombatState(CombatStates.ANGRY))
        {
            x = 1;
        }
        return x;
    }

    public double timer(IPokemob mob)
    {
        double x = 1;
        Entity entity = mob.getEntity();
        double alive = entity.ticksExisted;
        if (alive > 1500 && alive < 3001)
        {
            x = 2;
        }
        if (alive > 3000 && alive < 4501)
        {
            x = 3;
        }
        if (alive > 4500)
        {
            x = 4;
        }
        return x;
    }

    public double level(IPokemob mob)
    {
        MobEntity entity = mob.getEntity();
        int level = mob.getLevel();
        int otherLevel = 0;
        LivingEntity target = entity.getAttackTarget();
        IPokemob targetMob = CapabilityPokemob.getPokemobFor(target);
        if (targetMob == null || (otherLevel = targetMob.getLevel()) <= level) return 1;
        if (otherLevel <= 2 * level) return 2;
        if (otherLevel <= 4 * level) return 4;
        return 8;
    }

    public double lure(IPokemob mob)
    {
        MobEntity entity = mob.getEntity();
        if (mob.getPokedexEntry().swims())
        {// grow in 1.12
            AxisAlignedBB bb = Vector3.getNewVector().set(entity).addTo(0, entity.getEyeHeight(), 0).getAABB()
                    .grow(PokecubeMod.core.getConfig().fishHookBaitRange);
            List<EntityFishHook> hooks = entity.getEntityWorld().getEntitiesWithinAABB(EntityFishHook.class, bb);
            if (!hooks.isEmpty())
            {
                for (EntityFishHook hook : hooks)
                {
                    if (hook.caughtEntity == entity) return 5;
                }
            }
        }
        return 1;
    }

    public double moon(IPokemob mob)
    {
        if (mob.getPokedexEntry().canEvolve(1, PokecubeItems.getStack("moonstone"))) return 4;
        if (moonMatcher
                .matches(new SpawnCheck(Vector3.getNewVector().set(mob.getEntity()), mob.getEntity().getEntityWorld())))
            return 4;
        return 1;
    }

    public double love(IPokemob mob)
    {
        MobEntity entity = mob.getEntity();
        LivingEntity target = entity.getAttackTarget();
        IPokemob targetMob = CapabilityPokemob.getPokemobFor(target);
        if (targetMob == null || !(target instanceof AnimalEntity)) return 1;
        if (mob.canMate((AnimalEntity) target)) return 8;
        return 1;
    }

    public int heavy(IPokemob mob)
    {
        double mass = mob.getWeight();
        if (mass < 100) return -20;
        if (mass < 200) return 0;
        if (mass < 300) return 20;
        if (mass < 450) return 30;
        return 40;
    }

    public double fast(IPokemob mob)
    {
        return mob.getPokedexEntry().getStatVIT() < 100 ? 1 : 4;
    }

    public void luxury(IPokemob mob)
    {
        // Randomly increase happiness for being outside of pokecube.
        if (Math.random() > 0.999 && mob.getGeneralState(GeneralStates.TAMED))
        {
            HappinessType.applyHappiness(mob, HappinessType.TIME);
            HappinessType.applyHappiness(mob, HappinessType.TIME);
        }
    }
}
