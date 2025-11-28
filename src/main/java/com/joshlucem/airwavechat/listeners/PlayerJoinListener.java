package com.joshlucem.airwavechat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.joshlucem.airwavechat.manager.DataManager;
import com.joshlucem.airwavechat.manager.FrequencyManager;

public class PlayerJoinListener implements Listener {
    private final FrequencyManager frequencyManager;
    private final DataManager dataManager;

    public PlayerJoinListener(FrequencyManager frequencyManager, DataManager dataManager) {
        this.frequencyManager = frequencyManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Load and restore player's saved frequency
        dataManager.loadPlayerData(player, frequencyManager);
    }
}
