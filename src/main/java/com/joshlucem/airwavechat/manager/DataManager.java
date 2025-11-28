package com.joshlucem.airwavechat.manager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;

public class DataManager {
    private final AirwaveChat plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public DataManager(AirwaveChat plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        loadData();
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create playerdata.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void savePlayerFrequency(UUID playerId, String frequencyName) {
        dataConfig.set("players." + playerId.toString() + ".frequency", frequencyName);
        saveData();
    }

    public String getPlayerFrequency(UUID playerId) {
        return dataConfig.getString("players." + playerId.toString() + ".frequency");
    }

    public void removePlayerFrequency(UUID playerId) {
        dataConfig.set("players." + playerId.toString() + ".frequency", null);
        saveData();
    }

    public void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    public void loadPlayerData(Player player, FrequencyManager frequencyManager) {
        String freqName = getPlayerFrequency(player.getUniqueId());
        if (freqName != null) {
            FrequencyManager.Frequency freq = frequencyManager.getFrequency(freqName);
            if (freq != null) {
                // Reconnect player to their saved frequency
                frequencyManager.connect(player, freqName, freq.type);
            }
        }
    }
}
