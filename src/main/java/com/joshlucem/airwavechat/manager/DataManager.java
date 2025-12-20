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
                if (!dataFile.getParentFile().exists()) {
                    dataFile.getParentFile().mkdirs();
                }
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning(plugin.getMessage("error.create_data_error").replace("{error}", e.getMessage()));
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void savePlayerFrequency(UUID playerId, String frequencyName) {
        dataConfig.set("players." + playerId + ".frequency", frequencyName);
        saveDataAsync();
    }

    public String getPlayerFrequency(UUID playerId) {
        return dataConfig.getString("players." + playerId + ".frequency");
    }

    public void removePlayerFrequency(UUID playerId) {
        dataConfig.set("players." + playerId + ".frequency", null);
        saveDataAsync();
    }

    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe(plugin.getMessage("error.save_data_error").replace("{error}", e.getMessage()));
        }
    }

    private void saveDataAsync() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            org.bukkit.Bukkit.getAsyncScheduler().runNow(plugin, task -> saveData());
        } catch (ClassNotFoundException e) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::saveData);
        }
    }

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
