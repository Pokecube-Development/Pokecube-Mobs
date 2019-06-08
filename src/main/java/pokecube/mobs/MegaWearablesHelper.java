package pokecube.mobs;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import pokecube.core.PokecubeCore;
import pokecube.core.interfaces.PokecubeMod;
import pokecube.core.items.megastuff.ItemMegawearable;
import pokecube.core.items.megastuff.WearablesCompat;
import pokecube.core.items.megastuff.WearablesCompat.WearablesRenderer;
import thut.core.client.render.x3d.X3dModel;
import thut.wearables.EnumWearable;

public class MegaWearablesHelper
{
    public static void initExtraWearables()
    {
        // Tiara like worn by Lisia, but rotated to be centered on head instead
        // of at angle.
        ItemMegawearable.registerWearable("tiara", "HAT");
        WearablesCompat.renderers.put("tiara", new WearablesRenderer()
        {
            // 2 layers of hat rendering for the different colours.
            @OnlyIn(Dist.CLIENT)
            X3dModel                 model;

            // Textures for each hat layer.
            private ResourceLocation keystone = new ResourceLocation(PokecubeCore.ID, "textures/worn/keystone.png");
            private ResourceLocation metal    = new ResourceLocation(PokecubeCore.ID, "textures/worn/megatiara_2.png");

            @OnlyIn(Dist.CLIENT)
            @Override
            public void renderWearable(EnumWearable slot, LivingEntity wearer, ItemStack stack, float partialTicks)
            {
                if (slot != EnumWearable.HAT) return;
                if (model == null)
                {
                    model = new X3dModel(new ResourceLocation(PokecubeMod.ID, "models/worn/megatiara.x3d"));
                }
                Minecraft minecraft = Minecraft.getInstance();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glRotatef(180, 0, 0, 1);
                float dx = -0.0f, dy = .235f, dz = 0.25f;
                GL11.glTranslatef(dx, dy, dz);
                GlStateManager.pushMatrix();
                int brightness = wearer.getBrightnessForRender();
                int[] col = new int[] { 255, 255, 255, 255, brightness };
                minecraft.renderEngine.bindTexture(keystone);
                model.renderOnly("stone");
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                minecraft.renderEngine.bindTexture(metal);
                EnumDyeColor ret = EnumDyeColor.BLUE;
                if (stack.hasTag() && stack.getTag().hasKey("dyeColour"))
                {
                    int damage = stack.getTag().getInteger("dyeColour");
                    ret = EnumDyeColor.byDyeDamage(damage);
                }
                Color colour = new Color(ret.getColorValue() + 0xFF000000);
                col[0] = colour.getRed();
                col[2] = colour.getGreen();
                col[1] = colour.getBlue();
                model.getParts().get("tiara").setRGBAB(col);
                model.renderOnly("tiara");
                GL11.glColor3f(1, 1, 1);
                GlStateManager.popMatrix();

            }
        });

        // Mega Anklet like one worn by Zinnia
        ItemMegawearable.registerWearable("ankletzinnia", "ANKLE");
        WearablesCompat.renderers.put("ankletzinnia", new WearablesRenderer()
        {
            // 2 layers of hat rendering for the different colours.
            @OnlyIn(Dist.CLIENT)
            X3dModel                 model;

            // Textures for each hat layer.
            private ResourceLocation keystone = new ResourceLocation(PokecubeCore.ID, "textures/worn/keystone.png");
            private ResourceLocation texture  = new ResourceLocation(PokecubeCore.ID,
                    "textures/worn/megaankletzinnia_2.png");

            @Override
            public void renderWearable(EnumWearable slot, LivingEntity wearer, ItemStack stack, float partialTicks)
            {
                if (slot != EnumWearable.ANKLE) return;

                if (model == null)
                {
                    model = new X3dModel(new ResourceLocation(PokecubeMod.ID, "models/worn/megaankletzinnia.x3d"));
                }
                Minecraft minecraft = Minecraft.getInstance();
                GlStateManager.pushMatrix();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glRotatef(180, 0, 0, 1);
                float dx = -0.0f, dy = .05f, dz = 0.f;
                double s = 1;
                GL11.glScaled(s, s, s);
                GL11.glTranslatef(dx, dy, dz);
                int brightness = wearer.getBrightnessForRender();
                int[] col = new int[] { 255, 255, 255, 255, brightness };
                minecraft.renderEngine.bindTexture(keystone);
                model.renderOnly("stone");
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glScaled(s, s, s);
                GL11.glTranslatef(dx, dy, dz);
                minecraft.renderEngine.bindTexture(texture);
                EnumDyeColor ret = EnumDyeColor.CYAN;
                if (stack.hasTag() && stack.getTag().hasKey("dyeColour"))
                {
                    int damage = stack.getTag().getInteger("dyeColour");
                    ret = EnumDyeColor.byDyeDamage(damage);
                }
                Color colour = new Color(ret.getColorValue() + 0xFF000000);
                col[0] = colour.getRed();
                col[2] = colour.getGreen();
                col[1] = colour.getBlue();
                model.getParts().get("anklet").setRGBAB(col);
                model.renderOnly("anklet");
                GL11.glColor3f(1, 1, 1);
                GlStateManager.popMatrix();
            }
        });
        // a Pendant
        ItemMegawearable.registerWearable("pendant", "NECK");
        WearablesCompat.renderers.put("pendant", new WearablesRenderer()
        {
            // 2 layers of hat rendering for the different colours.
            @OnlyIn(Dist.CLIENT)
            X3dModel                 model;

            // Textures for each hat layer.
            private ResourceLocation keystone = new ResourceLocation(PokecubeCore.ID, "textures/worn/keystone.png");
            private ResourceLocation pendant  = new ResourceLocation(PokecubeCore.ID,
                    "textures/worn/megapendant_2.png");

            @OnlyIn(Dist.CLIENT)
            @Override
            public void renderWearable(EnumWearable slot, LivingEntity wearer, ItemStack stack, float partialTicks)
            {
                if (slot != EnumWearable.NECK) return;
                if (model == null)
                {
                    model = new X3dModel(new ResourceLocation(PokecubeMod.ID, "models/worn/megapendant.x3d"));
                }
                Minecraft minecraft = Minecraft.getInstance();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glRotatef(180, 0, 0, 1);
                float dx = -0.0f, dy = .0f, dz = 0.01f;
                GL11.glTranslatef(dx, dy, dz);
                GlStateManager.pushMatrix();
                int brightness = wearer.getBrightnessForRender();
                int[] col = new int[] { 255, 255, 255, 255, brightness };
                minecraft.renderEngine.bindTexture(keystone);
                model.renderOnly("keystone");
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                minecraft.renderEngine.bindTexture(pendant);
                EnumDyeColor ret = EnumDyeColor.YELLOW;
                if (stack.hasTag() && stack.getTag().hasKey("dyeColour"))
                {
                    int damage = stack.getTag().getInteger("dyeColour");
                    ret = EnumDyeColor.byDyeDamage(damage);
                }
                Color colour = new Color(ret.getColorValue() + 0xFF000000);
                col[0] = colour.getRed();
                col[2] = colour.getGreen();
                col[1] = colour.getBlue();
                model.getParts().get("pendant").setRGBAB(col);
                model.renderOnly("pendant");
                GL11.glColor3f(1, 1, 1);
                GlStateManager.popMatrix();

            }
        });
        // Earrings
        ItemMegawearable.registerWearable("earring", "EAR");
        WearablesCompat.renderers.put("earring", new WearablesRenderer()
        {
            // 2 layers of hat rendering for the different colours.
            @OnlyIn(Dist.CLIENT)
            X3dModel                 model;

            // Textures for each hat layer.
            private ResourceLocation keystone = new ResourceLocation(PokecubeCore.ID, "textures/worn/keystone.png");
            private ResourceLocation loop     = new ResourceLocation(PokecubeCore.ID,
                    "textures/worn/megaearring_2.png");

            @OnlyIn(Dist.CLIENT)
            @Override
            public void renderWearable(EnumWearable slot, LivingEntity wearer, ItemStack stack, float partialTicks)
            {
                if (slot != EnumWearable.EAR) return;
                if (model == null)
                {
                    model = new X3dModel(new ResourceLocation(PokecubeMod.ID, "models/worn/megaearring.x3d"));
                }
                Minecraft minecraft = Minecraft.getInstance();
                GL11.glRotatef(180, 0, 0, 1);
                float dx = -0.0f, dy = .0f, dz = -0.3f;
                GL11.glTranslatef(dx, dy, dz);
                GlStateManager.pushMatrix();
                int brightness = wearer.getBrightnessForRender();
                int[] col = new int[] { 255, 255, 255, 255, brightness };
                minecraft.renderEngine.bindTexture(keystone);
                model.renderOnly("keystone");
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                minecraft.renderEngine.bindTexture(loop);
                EnumDyeColor ret = EnumDyeColor.YELLOW;
                if (stack.hasTag() && stack.getTag().hasKey("dyeColour"))
                {
                    int damage = stack.getTag().getInteger("dyeColour");
                    ret = EnumDyeColor.byDyeDamage(damage);
                }
                Color colour = new Color(ret.getColorValue() + 0xFF000000);
                col[0] = colour.getRed();
                col[2] = colour.getGreen();
                col[1] = colour.getBlue();
                model.getParts().get("earing").setRGBAB(col);
                model.renderOnly("earing");
                GL11.glColor3f(1, 1, 1);
                GlStateManager.popMatrix();

            }
        });
        // Glasses
        ItemMegawearable.registerWearable("glasses", "EYE");
        WearablesCompat.renderers.put("glasses", new WearablesRenderer()
        {
            // 2 layers of hat rendering for the different colours.
            @OnlyIn(Dist.CLIENT)
            X3dModel                 model;

            // Textures for each hat layer.
            private ResourceLocation keystone = new ResourceLocation(PokecubeCore.ID, "textures/worn/keystone.png");
            private ResourceLocation loop     = new ResourceLocation(PokecubeCore.ID,
                    "textures/worn/megaglasses_2.png");

            @OnlyIn(Dist.CLIENT)
            @Override
            public void renderWearable(EnumWearable slot, LivingEntity wearer, ItemStack stack, float partialTicks)
            {
                if (slot != EnumWearable.EYE) return;
                if (model == null)
                {
                    model = new X3dModel(new ResourceLocation(PokecubeMod.ID, "models/worn/megaglasses.x3d"));
                }
                Minecraft minecraft = Minecraft.getInstance();
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glRotatef(180, 0, 0, 1);
                float dx = -0.0f, dy = .01f, dz = -0.25f;
                GL11.glTranslatef(dx, dy, dz);
                GlStateManager.pushMatrix();
                int brightness = wearer.getBrightnessForRender();
                int[] col = new int[] { 255, 255, 255, 255, brightness };
                minecraft.renderEngine.bindTexture(keystone);
                model.renderOnly("keystone");
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                minecraft.renderEngine.bindTexture(loop);
                EnumDyeColor ret = EnumDyeColor.GRAY;
                if (stack.hasTag() && stack.getTag().hasKey("dyeColour"))
                {
                    int damage = stack.getTag().getInteger("dyeColour");
                    ret = EnumDyeColor.byDyeDamage(damage);
                }
                Color colour = new Color(ret.getColorValue() + 0xFF000000);
                col[0] = colour.getRed();
                col[2] = colour.getGreen();
                col[1] = colour.getBlue();
                model.getParts().get("glasses").setRGBAB(col);
                model.renderOnly("glasses");
                GL11.glColor3f(1, 1, 1);
                GlStateManager.popMatrix();

            }
        });

    }
}
