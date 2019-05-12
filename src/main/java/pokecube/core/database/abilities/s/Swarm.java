package pokecube.core.database.abilities.s;

import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.IPokemob.MovePacket;
import pokecube.core.utils.PokeType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import pokecube.core.PokecubeCore;

public class Swarm extends Ability
{
    @Override
    public void onMoveUse(IPokemob mob, MovePacket move)
    {

        if (!move.pre) return;
        if (mob == move.attacker && move.attackType == PokeType.getType("bug")
                && mob.getEntity().getHealth() < mob.getEntity().getMaxHealth() / 3)
        {
            move.PWR *= 1.5;
        }
    }
    @Override
    public void onUpdate(IPokemob mob)
    {
      if (poke.ticksExisted % 20 == 0){
        if (!mob.getPokemonOwner() instanceof EntityPlayerMP) return;
        PokecubeCore.instance.spawner.doSpawnForPlayer(mob.getPokemonOwner(), mob.getPokemonOwner().getEntityWorld());
      }
    }
}
