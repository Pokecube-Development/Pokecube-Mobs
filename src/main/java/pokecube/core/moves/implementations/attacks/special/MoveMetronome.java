package pokecube.core.moves.implementations.attacks.special;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;

import net.minecraft.entity.Entity;
import pokecube.core.database.moves.MoveEntry;
import pokecube.core.interfaces.Move_Base;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.moves.MovesUtils;
import pokecube.core.moves.templates.Move_Basic;
import thut.api.maths.Vector3;

public class MoveMetronome extends Move_Basic
{
    public MoveMetronome()
    {
        super("metronome");
    }

    @Override
    public void ActualMoveUse(Entity user, Entity target, Vector3 start, Vector3 end)
    {
        Move_Base toUse = null;
        ArrayList<MoveEntry> moves = new ArrayList<MoveEntry>(MoveEntry.values());
        Collections.shuffle(moves);
        Iterator<MoveEntry> iter = moves.iterator();
        while (toUse == null && iter.hasNext())
        {
            MoveEntry move = iter.next();
            toUse = MovesUtils.getMoveFromName(move.name);
        }
        if (toUse != null) toUse.ActualMoveUse(user, target, start, end);
        else PokecubeMod.log(Level.WARNING, "Failed to find a move for metronome to use by " + user + " on " + target);
    }
}
