package pokecube.mobs.client.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import pokecube.core.interfaces.IPokemob;
import pokecube.core.interfaces.capabilities.CapabilityPokemob;
import thut.core.client.render.animation.ModelHolder;
import thut.core.client.render.model.IExtendedModelPart;
import thut.core.client.render.model.IModelRenderer;
import thut.core.client.render.model.IRetexturableModel;
import thut.core.client.render.wrappers.ModelWrapper;

public class ModelWrapperSpinda extends ModelWrapper
{
    private static final ResourceLocation normalh  = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaspotsh.png");
    private static final ResourceLocation normalhb = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaheadbase.png");
    private static final ResourceLocation shinyh   = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaspotshs.png");
    private static final ResourceLocation shinyhb  = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaheadbases.png");
    private static final ResourceLocation normale  = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaspotse.png");
    private static final ResourceLocation normaleb = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaearsbase.png");
    private static final ResourceLocation shinye   = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaspotses.png");
    private static final ResourceLocation shinyeb  = new ResourceLocation("pokecube_mobs",
            "gen_3/entity/textures/spindaearsbases.png");

    public ModelWrapperSpinda(ModelHolder model, IModelRenderer<?> renderer)
    {
        super(model, renderer);
    }

    /** Sets the models various rotation angles then renders the model. */
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch, float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        IPokemob spinda = CapabilityPokemob.getPokemobFor(entityIn);
        for (String partName : imodel.getParts().keySet())
        {
            IExtendedModelPart part = imodel.getParts().get(partName);
            if (part == null) continue;
            try
            {
                if (part.getParent() == null)
                {
                    Random rand = new Random(spinda.getRNGValue());
                    ((IRetexturableModel) part).setTexturer(null);

                    // Render the base layer of the head and ears
                    GlStateManager.pushMatrix();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(spinda.isShiny() ? shinyhb : normalhb);
                    part.renderOnly("Head");
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(spinda.isShiny() ? shinyeb : normaleb);
                    part.renderOnly("Left_ear");
                    GlStateManager.popMatrix();
                    GlStateManager.pushMatrix();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(spinda.isShiny() ? shinyeb : normaleb);
                    part.renderOnly("Right_ear");
                    GlStateManager.popMatrix();

                    // Render the 4 spots
                    for (int i = 0; i < 4; i++)
                    {
                        float dx = rand.nextFloat();
                        float dy = rand.nextFloat() / 2 + 0.5f;
                        GlStateManager.pushMatrix();
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        GL11.glTranslatef(dx, dy, 0.0F);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        Minecraft.getMinecraft().getTextureManager().bindTexture(spinda.isShiny() ? shinyh : normalh);
                        part.renderOnly("Head");
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GlStateManager.popMatrix();
                        GlStateManager.pushMatrix();
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        dx = rand.nextFloat();
                        dy = rand.nextFloat() / 2 + 0.5f;
                        GL11.glTranslatef(dx, dy, 0.0F);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        Minecraft.getMinecraft().getTextureManager().bindTexture(spinda.isShiny() ? shinye : normale);
                        part.renderOnly("Left_ear");
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GlStateManager.popMatrix();
                        GlStateManager.pushMatrix();
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        dx = rand.nextFloat();
                        dy = rand.nextFloat() / 2 + 0.5f;
                        GL11.glTranslatef(dx, dy, 0.0F);
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        part.renderOnly("Right_ear");
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GlStateManager.popMatrix();
                    }
                    // Render the model normally.
                    if (renderer.getTexturer() != null && part instanceof IRetexturableModel)
                    {
                        renderer.getTexturer().bindObject(entityIn);
                        ((IRetexturableModel) part).setTexturer(renderer.getTexturer());
                    }
                    GlStateManager.pushMatrix();
                    part.renderAll();
                    GlStateManager.popMatrix();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
