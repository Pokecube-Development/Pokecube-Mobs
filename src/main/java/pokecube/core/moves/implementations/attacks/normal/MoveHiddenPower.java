package pokecube.core.moves.implementations.attacks.normal;

import net.minecraft.entity.Entity;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.moves.templates.Move_Basic;
import pokecube.core.utils.PokeType;

public class MoveHiddenPower extends Move_Basic
{
    private static PokeType[] types;

    public MoveHiddenPower()
    {
        super("hiddenpower");
    }

    /** Type getter
     * 
     * @return the type of this move */
    @Override
    public PokeType getType(IPokemob user)
    {
        if (user == null) return move.type;

        if (types == null)
        {
            types = new PokeType[] { PokeType.getType("fighting"), PokeType.getType("flying"),
                    PokeType.getType("poison"), PokeType.getType("ground"), PokeType.getType("rock"),
                    PokeType.getType("bug"), PokeType.getType("ghost"), PokeType.getType("steel"),
                    PokeType.getType("fire"), PokeType.getType("water"), PokeType.getType("grass"),
                    PokeType.getType("electric"), PokeType.getType("psychic"), PokeType.getType("ice"),
                    PokeType.getType("dragon"), PokeType.getType("dark") };
        }
        int index = 0;
        byte[] ivs = user.getIVs();
        int a = (ivs[0] & 1);
        int b = (ivs[1] & 1);
        int c = (ivs[2] & 1);
        int d = (ivs[5] & 1);
        int e = (ivs[3] & 1);
        int f = (ivs[4] & 1);
        int abcdef = (a + 2 * b + 4 * c + 8 * d + 16 * e + 32 * f) * 15;
        index = abcdef / 63;
        return types[index];
    }

    /** PWR getter
     * 
     * @return the power of this move */
    @Override
    public int getPWR(IPokemob user, Entity target)
    {
        byte[] ivs = user.getIVs();
        int u = (ivs[0] & 2) / 2;
        int v = (ivs[1] & 2) / 2;
        int w = (ivs[2] & 2) / 2;
        int x = (ivs[5] & 2) / 2;
        int y = (ivs[3] & 2) / 2;
        int z = (ivs[4] & 2) / 2;
        int pwr = 30 + (u + 2 * v + 4 * w + 8 * x + 16 * y + 32 * z) * 40 / 63;
        return pwr;
    }
}
