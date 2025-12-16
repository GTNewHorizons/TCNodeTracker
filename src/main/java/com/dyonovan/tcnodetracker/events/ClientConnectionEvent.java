package com.dyonovan.tcnodetracker.events;

import java.io.File;
import java.net.InetSocketAddress;

import net.minecraft.client.Minecraft;
import net.minecraft.server.integrated.IntegratedServer;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.JsonUtils;
import com.dyonovan.tcnodetracker.lib.Utils;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientConnectionEvent {

    @SubscribeEvent
    public void onConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {

        String hostname;

        if (!event.isLocal) {

            InetSocketAddress address = (InetSocketAddress) event.manager.getSocketAddress();
            hostname = address.getHostName() + "_" + address.getPort();
            hostname = Utils.invalidChars(hostname);;

        } else {

            IntegratedServer server = Minecraft.getMinecraft().getIntegratedServer();
            hostname = (server != null) ? server.getFolderName() : "sp_world";
        }

        String hostname_old = "TCNodeTracker/" + Utils.invalidChars(hostname);
        hostname = "TCNodeTracker/" + hostname;

        File fileJson = new File(hostname);
        if (!fileJson.exists()) {
            File fileJsonOld = new File(hostname_old);
            if (fileJsonOld.exists()) {
                fileJsonOld.renameTo(fileJson);
            } else {
                fileJson.mkdirs();
            }
        }

        TCNodeTracker.hostName = hostname;
        TCNodeTracker.nodelist.clear();

        JsonUtils.readJson();
    }
}
