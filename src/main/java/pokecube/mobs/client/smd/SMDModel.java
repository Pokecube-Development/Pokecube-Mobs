package pokecube.mobs.client.smd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import pokecube.mobs.client.smd.impl.Bone;
import pokecube.mobs.client.smd.impl.Helpers;
import pokecube.mobs.client.smd.impl.Model;
import thut.core.client.render.animation.CapabilityAnimation.IAnimationHolder;
import thut.core.client.render.model.IAnimationChanger;
import thut.core.client.render.model.IExtendedModelPart;
import thut.core.client.render.model.IModel;
import thut.core.client.render.model.IModelCustom;
import thut.core.client.render.model.IModelRenderer;
import thut.core.client.render.model.IPartTexturer;
import thut.core.client.render.model.IRetexturableModel;
import thut.core.client.render.tabula.components.Animation;

public class SMDModel implements IModelCustom, IModel, IRetexturableModel, IFakeExtendedPart
{
    private final HashMap<String, IExtendedModelPart> nullPartsMap = Maps.newHashMap();
    private final HashMap<String, IExtendedModelPart> subPartsMap  = Maps.newHashMap();
    private final Set<String>                         nullHeadSet  = Sets.newHashSet();
    private final Set<String>                         animations   = Sets.newHashSet();
    private final HeadInfo                            info         = new HeadInfo();
    Model                                             wrapped;
    IPartTexturer                                     texturer;
    IAnimationChanger                                 changer;

    public SMDModel()
    {
        nullPartsMap.put(getName(), this);
    }

    public SMDModel(ResourceLocation model)
    {
        this();
        try
        {
            wrapped = new Model(model);
            wrapped.usesMaterials = true;
            animations.addAll(wrapped.anims.keySet());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void applyAnimation(Entity entity, IAnimationHolder animate, IModelRenderer<?> renderer, float partialTicks,
            float limbSwing)
    {
        wrapped.setAnimation(renderer.getAnimation(entity));
    }

    @Override
    public Set<String> getBuiltInAnimations()
    {
        return animations;
    }

    @Override
    public HeadInfo getHeadInfo()
    {
        return info;
    }

    @Override
    public Set<String> getHeadParts()
    {
        return nullHeadSet;
    }

    @Override
    public String getName()
    {
        return "main";
    }

    @Override
    public HashMap<String, IExtendedModelPart> getParts()
    {
        // SMD Renders whole thing at once, so no part rendering.
        return nullPartsMap;
    }

    @Override
    public HashMap<String, IExtendedModelPart> getSubParts()
    {
        return subPartsMap;
    }

    @Override
    public String getType()
    {
        return "smd";
    }

    @Override
    public void preProcessAnimations(Collection<List<Animation>> collection)
    {
        // TODO Bake these animations somehow for the particular SMD model.
    }

    public void render(IModelRenderer<?> renderer)
    {
        if (wrapped != null)
        {
            wrapped.body.setTexturer(texturer);
            wrapped.body.setAnimationChanger(changer);
            // Scaling factor for model.
            GL11.glScaled(0.165, 0.165, 0.165);
            // Makes model face correct way.
            GL11.glRotated(180, 0, 1, 0);

            // only increment frame if a tick has passed.
            if (wrapped.body.currentAnim != null && wrapped.body.currentAnim.frameCount() > 0)
            {
                wrapped.body.currentAnim.setCurrentFrame(info.currentTick % wrapped.body.currentAnim.frameCount());
            }
            // Check head parts for rendering rotations of them.
            for (String s : getHeadParts())
            {
                Bone bone = wrapped.body.getBone(s);
                if (bone != null)
                {
                    // Cap and convert pitch and yaw to radians.
                    float yaw = Math.max(Math.min(info.headYaw, info.yawCapMax), info.yawCapMin);
                    yaw = (float) Math.toRadians(yaw) * info.yawDirection;
                    float pitch = Math.max(Math.min(info.headPitch, info.pitchCapMax), info.pitchCapMin);
                    pitch = (float) Math.toRadians(pitch) * info.pitchDirection;

                    // Head rotation matrix
                    Matrix4f headRot = new Matrix4f();

                    float xr = 0, yr = 0, zr = 0;

                    switch (info.yawAxis)
                    {
                    case 2:
                        zr = yaw;
                        break;
                    case 1:
                        yr = yaw;
                        break;
                    case 0:
                        xr = yaw;
                        break;
                    }
                    headRot = Helpers.makeMatrix(0, 0, 0, xr, yr, zr);
                    // Apply the rotation.
                    bone.applyTransform(headRot);

                    xr = 0;
                    yr = 0;
                    zr = 0;

                    switch (info.pitchAxis)
                    {
                    case 2:
                        zr = pitch;
                        break;
                    case 1:
                        yr = pitch;
                        break;
                    case 0:
                        xr = pitch;
                        break;
                    }
                    headRot = Helpers.makeMatrix(0, 0, 0, xr, yr, zr);
                    // Apply the rotation.
                    bone.applyTransform(headRot);
                }
            }
            wrapped.animate();
            wrapped.renderAll();
        }
    }

    @Override
    public void renderAll(IModelRenderer<?> renderer)
    {
        render(renderer);
    }

    @Override
    public void renderAllExcept(IModelRenderer<?> renderer, String... excludedGroupNames)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void renderOnly(IModelRenderer<?> renderer, String... groupNames)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void renderPart(IModelRenderer<?> renderer, String partName)
    {
        // SMD Renders whole thing at once, so no part rendering.
        render(renderer);
    }

    @Override
    public void setAnimationChanger(IAnimationChanger changer)
    {
        this.changer = changer;
    }

    @Override
    public void setTexturer(IPartTexturer texturer)
    {
        this.texturer = texturer;
    }
}
