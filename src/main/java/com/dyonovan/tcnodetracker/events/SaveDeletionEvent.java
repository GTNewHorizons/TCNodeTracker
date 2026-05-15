package com.dyonovan.tcnodetracker.events;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraft.client.Minecraft;

import org.apache.commons.io.FileUtils;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.Constants;
import com.gtnewhorizon.gtnhlib.client.event.WorldDeletionEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SaveDeletionEvent {

    @SubscribeEvent
    public void onWorldDeletionEvent(WorldDeletionEvent event) {
        Path TCDataDir = Paths.get(Minecraft.getMinecraft().mcDataDir.getPath(), Constants.MODFOLDER, event.worldName);
        if (Files.isDirectory(TCDataDir)) {
            try {
                FileUtils.deleteDirectory(TCDataDir.toFile());
            } catch (IOException e) {
                TCNodeTracker.LOGGER.warn(
                        Constants.MODID + ": Failed to delete TCNodeTracker world data for world {}",
                        event.worldName);
            }
        }
    }
}
