package pokecube.core.database.abilities.i;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import pokecube.core.PokecubeCore;
import pokecube.core.database.abilities.Ability;
import pokecube.core.interfaces.IPokemob;

public class Illuminate extends Ability
{
    @Override
    public void onUpdate(IPokemob mob)
    {
        if (mob.getEntity().ticksExisted % 20 == 0)
        {
            if (!(mob.getPokemonOwner() instanceof EntityPlayerMP)) return;
            PokecubeCore.instance.spawner.doSpawnForPlayer((EntityPlayer) mob.getPokemonOwner(),
                    mob.getPokemonOwner().getEntityWorld());
        }
    }
}
