package com.dyonovan.tcnodetracker.events;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.dyonovan.tcnodetracker.TCNodeTracker;
import com.dyonovan.tcnodetracker.lib.JsonUtils;
import com.dyonovan.tcnodetracker.lib.NodeList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;

public class RightClickEvent {

    @SuppressWarnings({ "unchecked", "unused" })
    @SubscribeEvent
    public void playerRightClick(PlayerInteractEvent event) {

        if (event.isCanceled() || !event.entityPlayer.worldObj.isRemote
                || event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                || event.entityPlayer.inventory.getCurrentItem() == null) {
            return;
        }

        ItemStack heldItem = event.entityPlayer.inventory.getCurrentItem();

        if (!heldItem.getUnlocalizedName().equalsIgnoreCase("item.ItemThaumometer")) {
            return;
        }

        TileEntity i = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);

        if (i instanceof INode) {

            AspectList aspectList = ((INode) i).getAspects();
            if (aspectList.size() == 0) return;
            HashMap<String, Integer> hm = new HashMap<>();
            int dim = event.entityPlayer.worldObj.provider.dimensionId;

            for (Map.Entry<Aspect, Integer> entry : aspectList.aspects.entrySet()) {
                hm.put(entry.getKey().getTag(), entry.getValue());
            }

            String nodeType = ((INode) i).getNodeType().toString();
            String nodeMod = "BLANK";
            if (((INode) i).getNodeModifier() != null) {
                nodeMod = ((INode) i).getNodeModifier().toString();
            }

            if (TCNodeTracker.nodelist != null && !TCNodeTracker.nodelist.isEmpty()) {
                for (NodeList n : TCNodeTracker.nodelist) {
                    if (event.x == n.x && event.y == n.y && event.z == n.z && dim == n.dim) {
                        n.aspect = hm;
                        n.type = nodeType;
                        n.mod = nodeMod;
                        n.date = Instant.now();
                        JsonUtils.writeJson();
                        return;
                    }
                }
            }
            if (TCNodeTracker.nodelist == null) {
                TCNodeTracker.nodelist = new ArrayList<>();
            }
            TCNodeTracker.nodelist
                    .add(new NodeList(hm, dim, nodeType, nodeMod, event.x, event.y, event.z, Instant.now()));
            JsonUtils.writeJson();
        }
    }
}
