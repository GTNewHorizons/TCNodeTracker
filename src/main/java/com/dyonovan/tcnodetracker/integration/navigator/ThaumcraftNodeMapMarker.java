package com.dyonovan.tcnodetracker.integration.navigator;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.Constants;
import com.gtnewhorizons.navigator.api.model.markers.MapMarker;

import thaumcraft.api.aspects.Aspect;

public final class ThaumcraftNodeMapMarker {

    private static final ResourceLocation MARKED = new ResourceLocation(
            Constants.MODID,
            "textures/gui/node_marked.png");
    private static final ResourceLocation UNMARKED = new ResourceLocation(
            Constants.MODID,
            "textures/gui/node_unmarked.png");
    private static final int ICON_SIZE = 44;
    private static final int ASPECT_SIZE = 32;
    private static final Map<String, BufferedImage> ICONS = new HashMap<>();

    private ThaumcraftNodeMapMarker() {}

    public static @Nullable MapMarker create(ThaumcraftNodeLocation location) {
        Aspect aspect = location.getStrongestAspect();
        String key = aspect.getTag() + ':' + location.isActiveAsWaypoint();
        BufferedImage icon = ICONS.get(key);
        if (icon == null) {
            icon = compose(aspect, location.isActiveAsWaypoint());
            if (icon == null) return null;
            ICONS.put(key, icon);
        }
        return new MapMarker(icon).setDisplaySize(ICON_SIZE, ICON_SIZE).setDisplayZoomScale(1, 2, 3, 5);
    }

    private static @Nullable BufferedImage compose(Aspect aspect, boolean marked) {
        BufferedImage border = load(marked ? MARKED : UNMARKED);
        BufferedImage aspectImage = load(aspect.getImage());
        if (border == null || aspectImage == null) return null;
        if (!marked) tint(border, aspect.getColor());
        tint(aspectImage, aspect.getColor());

        BufferedImage image = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(border, 0, 0, ICON_SIZE, ICON_SIZE, null);
            int offset = (ICON_SIZE - ASPECT_SIZE) / 2;
            graphics.drawImage(aspectImage, offset, offset, ASPECT_SIZE, ASPECT_SIZE, null);
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static void tint(BufferedImage image, int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            pixels[i] = pixel & 0xFF000000 | (pixel >> 16 & 0xFF) * red / 255 << 16
                    | (pixel >> 8 & 0xFF) * green / 255 << 8
                    | (pixel & 0xFF) * blue / 255;
        }
        image.setRGB(0, 0, width, height, pixels, 0, width);
    }

    private static @Nullable BufferedImage load(ResourceLocation location) {
        try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location)
                .getInputStream()) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            TCNodeTracker.LOGGER.error("Could not load map marker image " + location, e);
            return null;
        }
    }
}
