package com.joshlucem.airwavechat.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.joshlucem.airwavechat.gui.GUIManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class StructureInteractListener implements Listener {
    private final GUIManager guiManager;

    public StructureInteractListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.REDSTONE_BLOCK) return;

        if (isValidStructure(block)) {
            event.setCancelled(true);
            guiManager.openClaimMenu(event.getPlayer());
            event.getPlayer().sendMessage(MessageUtil.color("<gold>Núcleo detectado. Reclama una frecuencia."));
        }
    }

    private boolean isValidStructure(Block core) {
        // center y0 redstone (core)
        // corners iron columns y-1,y0,y+1
        int[][] corners = new int[][] { {-1,-1}, {-1,1}, {1,-1}, {1,1} };
        for (int[] c : corners) {
            for (int dy = -1; dy <= 1; dy++) {
                if (core.getRelative(c[0], dy, c[1]).getType() != Material.IRON_BLOCK) return false;
            }
        }
        // copper below center (y-1) and above center (y+1)
        if (core.getRelative(0,-1,0).getType() != Material.COPPER_BLOCK) return false;
        if (core.getRelative(0,1,0).getType() != Material.COPPER_BLOCK) return false;
        return true;
    }
}
