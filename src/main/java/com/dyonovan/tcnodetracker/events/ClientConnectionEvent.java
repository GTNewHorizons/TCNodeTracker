package com.dyonovan.tcnodetracker.events;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.Constants;
import com.dyonovan.tcnodetracker.lib.JsonUtils;
import com.dyonovan.tcnodetracker.lib.Utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientConnectionEvent {

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        String worldDir;

        if (!event.isLocal) {
            InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
            worldDir = Utils.invalidChars(address.getHostName() + "_" + address.getPort());

        } else {
            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            worldDir = (server != null) ? server.getFolderName() : "sp_world";
        }

        Path storagePathRoot = (Minecraft.getMinecraft().mcDataDir.toPath()).resolve(Constants.MODFOLDER);
        Path storagePath = storagePathRoot.resolve(worldDir);
        Path storagePath_old = storagePathRoot.resolve(Utils.invalidChars(worldDir));

        if (Files.notExists(storagePath)) {
            try {
                if (Files.exists(storagePath_old)) {
                    Files.move(storagePath_old, storagePath);
                } else {
                    Files.createDirectories(storagePath);
                }
            } catch (IOException e) {
                TCNodeTracker.LOGGER.error("Failed to create or migrate node directory", e);
            }
        }

        TCNodeTracker.jsonPath = storagePath.resolve("nodes.json");
        TCNodeTracker.nodelist.clear();

        // Create empty JSON file if none exists yet to prevent log spam
        if (Files.notExists(TCNodeTracker.jsonPath)) {
            JsonUtils.writeJson();
        }
        JsonUtils.readJson();
    }
}
