package pokecube.adventures.legends.conditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import pokecube.adventures.legends.Condition;
import pokecube.core.database.Database;
import pokecube.core.database.PokedexEntry;
import pokecube.core.database.stats.CaptureStats;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.moves.implementations.actions.ActionTeleport;

public class Mew extends Condition
{
    @Override
    public boolean canCapture(Entity trainer)
    {
        return false;
    }

    @Override
    public boolean canCapture(Entity trainer, IPokemob pokemon)
    {
        int caught = CaptureStats.getNumberUniqueCaughtBy(trainer.getUniqueID());

        if (caught < Database.spawnables.size() - 1)
        {
            if (trainer instanceof EntityPlayer) ((EntityPlayer) trainer)
                    .sendMessage(new TextComponentString("You do not have enough badges to control Mew!"));
            ActionTeleport.teleportRandomly(pokemon.getEntity());
            return false;
        }
        return true;
    }

    @Override
    public PokedexEntry getEntry()
    {
        return Database.getEntry("Mew");
    }

}
