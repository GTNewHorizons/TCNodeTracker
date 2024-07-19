package com.dyonovan.tcnodetracker.bindings;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings {

    public static final KeyBinding aspectMenu = new KeyBinding("key.aspectMenu", Keyboard.KEY_I, "key.cat.tcnodetracker");
    public static final KeyBinding toggleNodeLayer = new KeyBinding("tcnodetracker.key.togglenode", Keyboard.KEY_NONE, "key.cat.tcnodetracker");

    public static void init() {
        ClientRegistry.registerKeyBinding(aspectMenu);

        if(TCNodeTracker.isNavigatorLoaded) {
            ClientRegistry.registerKeyBinding(toggleNodeLayer);
        }
    }
}
