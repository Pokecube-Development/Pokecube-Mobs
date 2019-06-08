package pokecube.mobs.client.smd.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Misc helper methods. */
public class Helpers
{
    static final Vector3f X_AXIS = new Vector3f(1.0F, 0.0F, 0.0F);
    static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);
    static final Vector3f Z_AXIS = new Vector3f(0.0F, 0.0F, 1.0F);

    /** Ensures that the given index will fit in the list.
     * 
     * @param list
     *            array to ensure has capacity
     * @param i
     *            index to check. */
    public static void ensureFits(ArrayList<?> list, int index)
    {
        while (list.size() <= index)
        {
            list.add(null);
        }
    }

    @SideOnly(Side.CLIENT)
    /** Gets an input stream for the given resourcelocation.
     * 
     * @param resloc
     * @return */
    public static BufferedInputStream getStream(ResourceLocation resloc)
    {
        try
        {
            return new BufferedInputStream(
                    Minecraft.getMinecraft().getResourceManager().getResource(resloc).getInputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Makes a new matrix4f for the given values This works as follows: A blank
     * matrix4f is made via new Matrix4f(), then the matrix is translated by x1,
     * y1, z1, and then it is rotated by zr, yr and xr, in that order, along
     * their respective axes. */
    public static Matrix4f makeMatrix(float xl, float yl, float zl, float xr, float yr, float zr)
    {
        Vector3f loc = new Vector3f(xl, yl, zl);
        Matrix4f part1 = new Matrix4f();
        part1.translate(loc);
        part1.rotate(zr, Z_AXIS);
        part1.rotate(yr, Y_AXIS);
        part1.rotate(xr, X_AXIS);
        return part1;
    }
}