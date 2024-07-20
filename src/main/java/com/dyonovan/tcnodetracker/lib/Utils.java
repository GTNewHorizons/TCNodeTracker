package com.dyonovan.tcnodetracker.lib;

import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public class Utils {

    public static final Pattern patternInvalidChars = Pattern.compile("[^a-zA-Z0-9_]");

    public static String invalidChars(String s) {

        return patternInvalidChars.matcher(s).replaceAll("_");
    }

    public static void drawAspect(double centerPixelX, double centerPixelY, double pixelSize, Aspect aspect,
            int amount) {
        final int textureSize = 16;

        GL11.glPushMatrix();
        final double scale = pixelSize / textureSize;
        GL11.glScaled(scale, scale, scale);
        UtilsFX.drawTag(
                (centerPixelX - pixelSize / 2) / scale,
                (centerPixelY - pixelSize / 2) / scale,
                aspect,
                amount,
                0,
                0,
                GL11.GL_ONE_MINUS_SRC_ALPHA,
                1.0F,
                false);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
