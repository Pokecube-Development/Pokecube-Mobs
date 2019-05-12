package pokecube.core.database.abilities.i;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import pokecube.core.database.abilities.Ability;
import pokecube.core.PokecubeCore;

public class Illuminate extends Ability
{
  @Override
  public void onUpdate(IPokemob mob)
  {
    if (poke.ticksExisted % 20 == 0){
      if (!mob.getPokemonOwner() instanceof EntityPlayerMP) return;
      PokecubeCore.instance.spawner.doSpawnForPlayer(mob.getPokemonOwner(), mob.getPokemonOwner().getEntityWorld());
    }
  }
}
