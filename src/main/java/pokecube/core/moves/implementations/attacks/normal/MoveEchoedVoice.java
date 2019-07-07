package pokecube.core.moves.implementations.attacks.normal;

import net.minecraft.entity.Entity;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.pokemob.moves.MovePacket;
import pokecube.core.moves.templates.Move_Basic;

public class MoveEchoedVoice extends Move_Basic
{

    public MoveEchoedVoice()
    {
        super("echoedvoice");
    }

    @Override
    public void onAttack(MovePacket packet)
    {
        super.onAttack(packet);
        packet.attacker.getMoveStats().FURYCUTTERCOUNTER++;
    }

    @Override
    public int getPWR(IPokemob attacker, Entity attacked)
    {
        double rollOut = attacker.getMoveStats().FURYCUTTERCOUNTER;
        int PWR = (int) Math.max(this.getPWR(), Math.min(200, (rollOut * 2) * this.getPWR()));
        return PWR;
    }
}