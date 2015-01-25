package com.dyonovan.tcnodetracker;

import com.dyonovan.tcnodetracker.bindings.KeyBindings;
import com.dyonovan.tcnodetracker.events.ClientTick;
import com.dyonovan.tcnodetracker.events.ClientConnectionEvent;
import com.dyonovan.tcnodetracker.events.KeyInputEvent;
import com.dyonovan.tcnodetracker.events.RightClickEvent;
import com.dyonovan.tcnodetracker.lib.Constants;
import com.dyonovan.tcnodetracker.lib.NodeList;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

@Mod(name = Constants.MODNAME, modid = Constants.MODID, version = Constants.VERSION, dependencies = Constants.DEPENDENCIES,
        acceptableRemoteVersions = "*")

public class TCNodeTracker {

    public static String hostName;
    public static ArrayList<NodeList> nodelist = new ArrayList<>();

    @Instance(Constants.MODID)
    public static TCNodeTracker instance;

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new RightClickEvent());
        FMLCommonHandler.instance().bus().register(new ClientConnectionEvent());
        FMLCommonHandler.instance().bus().register(new ClientTick());
        FMLCommonHandler.instance().bus().register(new KeyInputEvent());

    }

    @SideOnly(Side.CLIENT)
    @EventHandler
    public void init(FMLInitializationEvent event) {
        KeyBindings.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

}