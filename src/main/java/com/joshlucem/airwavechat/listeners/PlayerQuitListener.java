package com.joshlucem.airwavechat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.joshlucem.airwavechat.manager.FrequencyManager;

public class PlayerQuitListener implements Listener {
    private final FrequencyManager frequencyManager;

    public PlayerQuitListener(FrequencyManager frequencyManager) {
        this.frequencyManager = frequencyManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Auto-disconnect from frequency on logout
        frequencyManager.disconnect(player);
    }
}
