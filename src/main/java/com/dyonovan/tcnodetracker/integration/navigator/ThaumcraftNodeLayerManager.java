package com.dyonovan.tcnodetracker.integration.navigator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.integration.navigator.journeymap.JMThaumcraftNodeRenderer;
import com.dyonovan.tcnodetracker.integration.navigator.journeymap.JMThaumcraftNodeWaypointManager;
import com.dyonovan.tcnodetracker.integration.navigator.xaero.XaeroThaumcraftNodeRenderer;
import com.dyonovan.tcnodetracker.integration.navigator.xaero.XaeroThaumcraftNodeWaypointManager;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.locations.IWaypointAndLocationProvider;
import com.gtnewhorizons.navigator.api.model.waypoints.WaypointManager;

public class ThaumcraftNodeLayerManager extends InteractableLayerManager {

    public static final ThaumcraftNodeLayerManager instance = new ThaumcraftNodeLayerManager();

    private int oldMinBlockX = 0;
    private int oldMinBlockZ = 0;
    private int oldMaxBlockX = 0;
    private int oldMaxBlockZ = 0;

    public ThaumcraftNodeLayerManager() {
        super(ThaumcraftNodeButtonManager.instance);
    }

    @Override
    protected boolean needsRegenerateVisibleElements(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ) {
        if (minBlockX != oldMinBlockX || minBlockZ != oldMinBlockZ
                || maxBlockX != oldMaxBlockX
                || maxBlockZ != oldMaxBlockZ) {
            oldMinBlockX = minBlockX;
            oldMinBlockZ = minBlockZ;
            oldMaxBlockX = maxBlockX;
            oldMaxBlockZ = maxBlockZ;
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    protected LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        return switch (mod) {
            case JourneyMap -> new JMThaumcraftNodeRenderer(manager);
            case XaeroWorldMap -> new XaeroThaumcraftNodeRenderer(manager);
            default -> null;
        };
    }

    @Nullable
    @Override
    protected WaypointManager addWaypointManager(InteractableLayerManager manager, SupportedMods mod) {
        return switch (mod) {
            case JourneyMap -> new JMThaumcraftNodeWaypointManager(manager);
            case XaeroWorldMap -> new XaeroThaumcraftNodeWaypointManager(manager);
            default -> null;
        };
    }

    @Override
    protected List<? extends IWaypointAndLocationProvider> generateVisibleElements(int minBlockX, int minBlockZ,
            int maxBlockX, int maxBlockZ) {
        final int playerDimensionId = Minecraft.getMinecraft().thePlayer.dimension;

        ArrayList<ThaumcraftNodeLocation> thaumcraftNodeLocations = new ArrayList<>();

        for (NodeList node : TCNodeTracker.nodelist) {
            if (node.dim == playerDimensionId && node.x >= minBlockX
                    && node.x <= maxBlockX
                    && node.z >= minBlockZ
                    && node.z <= maxBlockZ) {
                thaumcraftNodeLocations.add(new ThaumcraftNodeLocation(node));
            }
        }

        return thaumcraftNodeLocations;
    }

    public void deleteNode(ThaumcraftNodeLocation thaumcraftNodeLocation) {
        TCNodeTracker.nodelist.removeIf(thaumcraftNodeLocation::belongsToNode);
        if (thaumcraftNodeLocation.isActiveAsWaypoint()) {
            clearActiveWaypoint();
        }
        forceRefresh();
    }
}
