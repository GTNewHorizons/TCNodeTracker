package com.dyonovan.tcnodetracker.integration.navigator;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.dyonovan.tcnodetracker.lib.Constants;
import com.dyonovan.tcnodetracker.lib.Utils;
import com.gtnewhorizons.navigator.api.model.steps.UniversalInteractableStep;
import com.gtnewhorizons.navigator.api.util.DrawUtils;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

public final class ThaumcraftNodeRenderStep extends UniversalInteractableStep<ThaumcraftNodeLocation> {

    private static final ResourceLocation MARKED = new ResourceLocation(
            Constants.MODID,
            "textures/gui/node_marked.png");
    private static final ResourceLocation UNMARKED = new ResourceLocation(
            Constants.MODID,
            "textures/gui/node_unmarked.png");
    private static final int ICON_SIZE = 44;
    private static final int ASPECT_SIZE = 32;

    public ThaumcraftNodeRenderStep(ThaumcraftNodeLocation location) {
        super(location);
        setMinScale(0.5);
    }

    @Override
    public void preRender(double centerX, double centerY, float drawScale, double zoom) {
        double scale = isXaero ? getScaling(zoom) : drawScale;
        setSize(ICON_SIZE * scale);
        setOffset(-width / 2);
    }

    @Override
    public void draw(double x, double y, float drawScale, double zoom) {
        boolean marked = location.isActiveAsWaypoint();
        DrawUtils.drawQuad(
                marked ? MARKED : UNMARKED,
                x,
                y,
                width,
                height,
                marked ? 0xFFFFFF : location.getStrongestAspect().getColor(),
                204);

        double aspectSize = isXaero ? ASPECT_SIZE * getScaling(zoom) : ASPECT_SIZE;
        Utils.drawAspect(x + width / 2, y + height / 2, aspectSize, location.getStrongestAspect(), 0);
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        double deltaX = mouseX - (getX() + width / 2);
        double deltaY = mouseY - (getY() + height / 2);
        double radius = width / 2;
        return deltaX * deltaX + deltaY * deltaY <= radius * radius;
    }

    @Override
    public void getTooltip(List<String> tooltip) {}

    @Override
    public void onActionKeyPressed() {
        ThaumcraftNodeLayerManager.instance.deleteNode(location);
    }

    @Override
    public void drawCustomTooltip(FontRenderer fontRenderer, int mouseX, int mouseY, int displayWidth,
            int displayHeight) {
        boolean waypoint = location.isActiveAsWaypoint();
        String waypointHint = location.getActiveWaypointHint();
        String title = location.getTitle();
        String description = location.getDescription();
        String deleteHint = location.getDeleteHint();

        int maxTextWidth = Math.max(
                Math.max(fontRenderer.getStringWidth(title), fontRenderer.getStringWidth(description)),
                fontRenderer.getStringWidth(deleteHint));
        if (waypoint) maxTextWidth = Math.max(maxTextWidth, fontRenderer.getStringWidth(waypointHint));
        if (fontRenderer.getBidiFlag()) maxTextWidth = (int) Math.ceil(maxTextWidth * 1.25F);

        int aspectRows = (location.getAspects().size() + 4) / 5;
        int aspectColumns = Math.min(location.getAspects().size(), 5);
        int tooltipHeight = (waypoint ? 44 : 32) + aspectRows * 16;
        int tooltipWidth = Math.max(aspectColumns * 16, maxTextWidth);
        int pixelX = mouseX + 12;
        int pixelY = mouseY - 12;
        if (pixelX + tooltipWidth > displayWidth) pixelX -= 28 + tooltipWidth;
        if (pixelY + tooltipHeight + 6 > displayHeight) pixelY = displayHeight - tooltipHeight - 6;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        int backgroundColor = 0xF0100010;
        DrawUtils.drawGradientRect(
                pixelX - 3,
                pixelY - 4,
                pixelX + tooltipWidth + 3,
                pixelY - 3,
                backgroundColor,
                backgroundColor);
        DrawUtils.drawGradientRect(
                pixelX - 3,
                pixelY + tooltipHeight + 3,
                pixelX + tooltipWidth + 3,
                pixelY + tooltipHeight + 4,
                backgroundColor,
                backgroundColor);
        DrawUtils.drawGradientRect(
                pixelX - 3,
                pixelY - 3,
                pixelX + tooltipWidth + 3,
                pixelY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor);
        DrawUtils.drawGradientRect(
                pixelX - 4,
                pixelY - 3,
                pixelX - 3,
                pixelY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor);
        DrawUtils.drawGradientRect(
                pixelX + tooltipWidth + 3,
                pixelY - 3,
                pixelX + tooltipWidth + 4,
                pixelY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor);

        int topBorder = 0x505000FF;
        int bottomBorder = 0x5028007F;
        DrawUtils.drawGradientRect(
                pixelX - 3,
                pixelY - 2,
                pixelX - 2,
                pixelY + tooltipHeight + 2,
                topBorder,
                bottomBorder);
        DrawUtils.drawGradientRect(
                pixelX + tooltipWidth + 2,
                pixelY - 2,
                pixelX + tooltipWidth + 3,
                pixelY + tooltipHeight + 2,
                topBorder,
                bottomBorder);
        DrawUtils.drawGradientRect(pixelX - 3, pixelY - 3, pixelX + tooltipWidth + 3, pixelY - 2, topBorder, topBorder);
        DrawUtils.drawGradientRect(
                pixelX - 3,
                pixelY + tooltipHeight + 2,
                pixelX + tooltipWidth + 3,
                pixelY + tooltipHeight + 3,
                bottomBorder,
                bottomBorder);

        int offset = 0;
        if (fontRenderer.getBidiFlag()) {
            if (waypoint) {
                drawRightAligned(fontRenderer, waypointHint, pixelX, pixelY, tooltipWidth);
                offset += 12;
            }
            drawRightAligned(fontRenderer, title, pixelX, pixelY + offset, tooltipWidth);
            offset += 12;
            drawRightAligned(fontRenderer, description, pixelX, pixelY + offset, tooltipWidth);
            drawRightAligned(fontRenderer, deleteHint, pixelX, pixelY + aspectRows * 16 + offset + 12, tooltipWidth);
        } else {
            if (waypoint) {
                fontRenderer.drawString(waypointHint, pixelX, pixelY, 0xFFFFFFFF);
                offset += 12;
            }
            fontRenderer.drawString(title, pixelX, pixelY + offset, 0xFFFFFFFF);
            offset += 12;
            fontRenderer.drawString(description, pixelX, pixelY + offset, 0xFFFFFFFF);
            fontRenderer.drawString(deleteHint, pixelX, pixelY + aspectRows * 16 + offset + 12, 0xFFFFFFFF);
        }

        int aspectX = 0;
        int aspectY = 0;
        for (Aspect aspect : location.getAspects().getAspectsSortedAmount()) {
            GL11.glPushMatrix();
            UtilsFX.drawTag(
                    pixelX + aspectX * 16,
                    pixelY + aspectY * 16 + offset + 10,
                    aspect,
                    location.getAspects().getAmount(aspect),
                    0,
                    0.01,
                    1,
                    1,
                    false);
            GL11.glPopMatrix();
            if (++aspectX >= 5) {
                aspectX = 0;
                aspectY++;
            }
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }

    private static void drawRightAligned(FontRenderer fontRenderer, String text, int x, int y, int width) {
        int textWidth = (int) Math.ceil(fontRenderer.getStringWidth(text) * 1.1F);
        fontRenderer.drawString(text, x + width - textWidth, y, 0xFFFFFFFF);
    }
}
