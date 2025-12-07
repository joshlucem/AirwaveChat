package com.joshlucem.airwavechat.manager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;

/**
 * Manages persistent player data, including frequency connections.
 * Handles asynchronous file I/O to prevent blocking the main thread.
 */
public class DataManager {
    private final AirwaveChat plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(AirwaveChat plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        loadData();
    }

    /**
     * Load data from disk.
     */
    private void loadData() {
        if (!dataFile.exists()) {
            try {
                if (!dataFile.getParentFile().exists()) {
                    dataFile.getParentFile().mkdirs();
                }
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create playerdata.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    /**
     * Save player frequency asynchronously to prevent blocking.
     */
    public void savePlayerFrequency(UUID playerId, String frequencyName) {
        dataConfig.set("players." + playerId + ".frequency", frequencyName);
        saveDataAsync();
    }

    /**
     * Get player's saved frequency.
     */
    public String getPlayerFrequency(UUID playerId) {
        return dataConfig.getString("players." + playerId + ".frequency");
    }

    /**
     * Remove player frequency record.
     */
    public void removePlayerFrequency(UUID playerId) {
        dataConfig.set("players." + playerId + ".frequency", null);
        saveDataAsync();
    }

    /**
     * Save data to disk synchronously.
     */
    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    /**
     * Save data asynchronously to avoid blocking the main thread.
     */
    private void saveDataAsync() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveData);
    }

    /**
     * Load player's saved frequency and reconnect them.
     */
    public void loadPlayerData(Player player, FrequencyManager frequencyManager) {
        String freqName = getPlayerFrequency(player.getUniqueId());
        if (freqName != null && !freqName.isEmpty()) {
            FrequencyManager.Frequency freq = frequencyManager.getFrequency(freqName);
            if (freq != null) {
                frequencyManager.connect(player, freqName, freq.type);
            }
        }
    }
}
