package pokecube.mobs.client.smd.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;

/** Base model object, this contains the body, a list of the bones, and the
 * animations. */
public class Model
{
    public Body                       body;
    public HashMap<String, Animation> anims         = new HashMap<String, Animation>();
    public Bone                       root;
    public ArrayList<Bone>            allBones;
    public Animation                  currentAnimation;
    public boolean                    hasAnimations = true;
    public boolean                    usesMaterials = true;

    public Model(Model model)
    {
        this.body = new Body(model.body, this);
        Iterator<Map.Entry<String, Animation>> iterator = model.anims.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, Animation> entry = iterator.next();
            this.anims.put(entry.getKey(), new Animation(entry.getValue(), this));
        }
        this.hasAnimations = model.hasAnimations;
        this.usesMaterials = model.usesMaterials;
        this.currentAnimation = this.anims.get("idle");
    }

    public Model(ResourceLocation resource) throws Exception
    {
        loadAnimationsAndModel(resource);
        reformBones();
        precalculateAnims();
    }

    public void animate()
    {
        resetVerts(this.body);
        if (this.body.currentAnim == null)
        {
            setAnimation("idle");
        }
        this.root.prepareTransform();
        for (Bone b : this.allBones)
        {
            b.applyTransform();
        }
        applyVertChange(this.body);
    }

    private void applyVertChange(Body body)
    {
        if (body == null) { return; }
        for (MutableVertex v : body.verts)
        {
            v.apply();
        }
    }

    public boolean hasAnimations()
    {
        return this.hasAnimations;
    }

    private void loadAnimationsAndModel(ResourceLocation resloc) throws Exception
    {
        try
        {
            ResourceLocation modelPath = resloc;
            // Load the model.
            this.body = new Body(this, modelPath);

            List<String> anims = Lists.newArrayList("idle", "walking", "flying", "sleeping", "swimming");
            String resLoc = resloc.toString();
            // Check for valid animations, and load them in as well.
            for (String s : anims)
            {
                String anim = resLoc.endsWith("smd") ? resLoc.replace(".smd", "/" + s + ".smd")
                        : resLoc.replace(".SMD", "/" + s + ".smd");
                ResourceLocation animation = new ResourceLocation(anim);
                try
                {
                    this.anims.put(s, new Animation(this, s, animation));
                    if (s.equalsIgnoreCase("idle"))
                    {
                        this.currentAnimation = this.anims.get(s);
                    }
                }
                catch (Exception e)
                {
                    // e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    private void precalculateAnims()
    {
        for (Animation anim : this.anims.values())
        {
            anim.precalculateAnimation(this.body);
        }
    }

    private void reformBones()
    {
        this.root.applyChildrenToRest();
        for (Bone b : this.allBones)
        {
            b.invertRestMatrix();
        }
    }

    public void renderAll()
    {
        GL11.glShadeModel(GL11.GL_SMOOTH);
        this.body.render();
        GL11.glShadeModel(GL11.GL_FLAT);
    }

    private void resetVerts(Body body)
    {
        if (body == null) { return; }
        for (MutableVertex v : body.verts)
        {
            v.reset();
        }
    }

    public void setAnimation(String name)
    {
        Animation old = this.currentAnimation;
        if (this.anims.containsKey(name))
        {
            this.currentAnimation = this.anims.get(name);
        }
        else
        {
            this.currentAnimation = this.anims.get("idle");
        }
        this.body.setAnimation(this.currentAnimation);
        if (old != this.currentAnimation)
        {
        }
    }

    void syncBones(Body body)
    {
        this.allBones = body.bones;
        if (!body.partOfGroup)
        {
            this.root = body.root;
        }
    }
}