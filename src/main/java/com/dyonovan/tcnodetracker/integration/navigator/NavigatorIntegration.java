package com.dyonovan.tcnodetracker.integration.navigator;

import com.gtnewhorizons.navigator.api.NavigatorApi;

public class NavigatorIntegration {

    public static void init() {
        // Forge initializes this class if it's put in the @Mod class even if it's behind a Loader.isModLoaded flag
        NavigatorApi.registerLayerManager(ThaumcraftNodeLayerManager.instance);
    }
}
