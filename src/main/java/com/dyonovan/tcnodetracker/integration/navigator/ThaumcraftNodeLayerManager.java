package com.dyonovan.tcnodetracker.integration.navigator;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nullable;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.integration.navigator.journeymap.JMThaumcraftNodeWaypointManager;
import com.dyonovan.tcnodetracker.integration.navigator.xaero.XaeroThaumcraftNodeWaypointManager;
import com.dyonovan.tcnodetracker.lib.NodeList;
import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.layers.InteractableLayerManager;
import com.gtnewhorizons.navigator.api.model.layers.LayerRenderer;
import com.gtnewhorizons.navigator.api.model.layers.UniversalInteractableRenderer;
import com.gtnewhorizons.navigator.api.model.locations.IWaypointAndLocationProvider;
import com.gtnewhorizons.navigator.api.model.waypoints.WaypointManager;

public class ThaumcraftNodeLayerManager extends InteractableLayerManager {

    public static final ThaumcraftNodeLayerManager instance = new ThaumcraftNodeLayerManager();

    public ThaumcraftNodeLayerManager() {
        super(ThaumcraftNodeButtonManager.instance);
    }

    @Nullable
    @Override
    protected LayerRenderer addLayerRenderer(InteractableLayerManager manager, SupportedMods mod) {
        return new UniversalInteractableRenderer(manager)
                .withRenderStep(location -> new ThaumcraftNodeRenderStep((ThaumcraftNodeLocation) location))
                .withMapMarker(location -> ThaumcraftNodeMapMarker.create((ThaumcraftNodeLocation) location));
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
    protected Collection<? extends IWaypointAndLocationProvider> generateVisibleLocations(int minBlockX, int minBlockZ,
            int maxBlockX, int maxBlockZ, int dimension) {
        ArrayList<ThaumcraftNodeLocation> thaumcraftNodeLocations = new ArrayList<>();

        for (NodeList node : TCNodeTracker.nodelist) {
            if (node.dim == dimension && node.x >= minBlockX
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
        removeLocation(thaumcraftNodeLocation);
    }
}
