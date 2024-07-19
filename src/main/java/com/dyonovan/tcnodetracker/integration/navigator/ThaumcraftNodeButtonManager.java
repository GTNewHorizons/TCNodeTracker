package com.dyonovan.tcnodetracker.integration.navigator;

import com.dyonovan.tcnodetracker.lib.Constants;
import com.gtnewhorizons.navigator.api.model.SupportedMods;
import com.gtnewhorizons.navigator.api.model.buttons.ButtonManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class ThaumcraftNodeButtonManager extends ButtonManager {

    public static final ThaumcraftNodeButtonManager instance = new ThaumcraftNodeButtonManager();

    @Override
    public ResourceLocation getIcon(SupportedMods mod, String theme) {
        return new ResourceLocation(Constants.MODID, "textures/gui/node_icon.png");
    }

    @Override
    public String getButtonText() {
        return StatCollector.translateToLocal("tcnodetracker.button.nodes");
    }
}
