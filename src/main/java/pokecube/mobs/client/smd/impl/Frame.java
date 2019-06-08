package pokecube.mobs.client.smd.impl;

import java.util.ArrayList;

import com.google.common.collect.Lists;

/** This is a section of an animation, it specifics a particular set of
 * transformation matrices, each one is for a different bone. */
public class Frame
{
    public final int           ID;
    public Animation           owner;
    public ArrayList<Matrix4f> invertTransforms = Lists.newArrayList();
    public ArrayList<Matrix4f> transforms       = Lists.newArrayList();

    public Frame(Animation parent)
    {
        this.owner = parent;
        this.ID = parent.newFrameID();
    }

    public Frame(Frame anim, Animation parent)
    {
        this.owner = parent;
        this.ID = anim.ID;
        this.transforms = anim.transforms;
        this.invertTransforms = anim.invertTransforms;
    }

    public void addTransforms(int index, Matrix4f invertedData)
    {
        this.transforms.add(index, invertedData);
        this.invertTransforms.add(index, Matrix4f.invert(invertedData, null));
    }

    /** Applies the appropriate transforms to the various bones. */
    public void applyTransforms()
    {
        for (int i = 0; i < this.transforms.size(); i++)
        {
            Bone bone = this.owner.bones.get(i);
            if (bone.parent != null)
            {
                Matrix4f temp = Matrix4f.mul(this.transforms.get(bone.parent.ID), this.transforms.get(i), null);
                this.transforms.set(i, temp);
                this.invertTransforms.set(i, Matrix4f.invert(temp, null));
            }
        }
    }

    /** Sets up the transforms for the given index.
     * 
     * @param id
     *            - transform index
     * @param degrees */
    public void setTransforms(int id)
    {
        Matrix4f rotator = Helpers.makeMatrix(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        Matrix4f.mul(rotator, this.transforms.get(id), this.transforms.get(id));
        Matrix4f.mul(Matrix4f.invert(rotator, null), this.invertTransforms.get(id), this.invertTransforms.get(id));
    }
}