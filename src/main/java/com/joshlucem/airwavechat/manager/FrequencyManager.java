package com.joshlucem.airwavechat.manager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;

/**
 * Manages frequency creation, player connections, and data persistence.
 * Thread-safe using ConcurrentHashMap for concurrent access.
 */
public class FrequencyManager {
    private final AirwaveChat plugin;
    
    /**
     * Represents a radio frequency with listener tracking.
     */
    public static class Frequency {
        public final String name;
        public final String type; // AM or FM
        public final double chatDistance;
        public final Set<UUID> listeners;

        public Frequency(String name, String type, double chatDistance) {
            this.name = name;
            this.type = type;
            this.chatDistance = chatDistance;
            this.listeners = ConcurrentHashMap.newKeySet();
        }
    }

    private final Map<String, Frequency> frequencies;
    private final Map<UUID, String> playerFrequency;

    public FrequencyManager(FileConfiguration config, AirwaveChat plugin) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null");
        if (config == null) throw new IllegalArgumentException("Config cannot be null");
        
        this.plugin = plugin;
        this.frequencies = new ConcurrentHashMap<>();
        this.playerFrequency = new ConcurrentHashMap<>();
        
        // Load frequencies from configuration
        loadFrequencies(config);
    }

    /**
     * Load all frequencies from configuration efficiently.
     */
    private void loadFrequencies(FileConfiguration config) {
        boolean allowCustom = config.getBoolean("frequencies.allow_custom", true);
        
        // Load FM frequencies
        loadFMFrequencies(config);
        
        // Load AM frequencies
        loadAMFrequencies(config);
        
        // Load custom frequencies
        if (allowCustom) {
            loadCustomFrequencies(config);
        }
    }

    /**
     * Load FM frequencies from config.
     */
    private void loadFMFrequencies(FileConfiguration config) {
        double fmMin = config.getDouble("frequencies.fm.min", 100.0);
        double fmMax = config.getDouble("frequencies.fm.max", 199.9);
        double fmStep = config.getDouble("frequencies.fm.step", 0.1);
        double fmDistance = config.getDouble("frequencies.fm.chat_distance", 40.0);
        int fmMaxCount = config.getInt("frequencies.fm.max_count", 1000);
        
        int count = 0;
        for (double f = fmMin; f <= fmMax && count < fmMaxCount; f += fmStep) {
            String freq = String.format("%.1f", f);
            frequencies.put(freq, new Frequency(freq, "FM", fmDistance));
            count++;
        }
    }

    /**
     * Load AM frequencies from config.
     */
    private void loadAMFrequencies(FileConfiguration config) {
        int amMin = config.getInt("frequencies.am.min", 1000);
        int amMax = config.getInt("frequencies.am.max", 1999);
        int amStep = config.getInt("frequencies.am.step", 1);
        double amDistance = config.getDouble("frequencies.am.chat_distance", 100.0);
        int amMaxCount = config.getInt("frequencies.am.max_count", 1000);
        
        int count = 0;
        for (int a = amMin; a <= amMax && count < amMaxCount; a += amStep) {
            String freq = Integer.toString(a);
            frequencies.put(freq, new Frequency(freq, "AM", amDistance));
            count++;
        }
    }

    /**
     * Load custom frequencies from config.
     */
    private void loadCustomFrequencies(FileConfiguration config) {
        if (!config.isConfigurationSection("frequencies.custom")) {
            return;
        }
        
        var customSection = config.getConfigurationSection("frequencies.custom");
        if (customSection == null) return;
        
        double fmDefault = config.getDouble("frequencies.fm.default", 101.1);
        double amDefault = config.getInt("frequencies.am.default", 1000);
        double fmDistance = config.getDouble("frequencies.fm.chat_distance", 40.0);
        double amDistance = config.getDouble("frequencies.am.chat_distance", 100.0);
        
        for (String key : customSection.getKeys(false)) {
            String type = customSection.getString(key + ".type", "FM");
            double value = getConfigDouble(customSection, key + ".frequency", 
                type.equalsIgnoreCase("AM") ? amDefault : fmDefault);
            double distance = getConfigDouble(customSection, key + ".chat_distance",
                type.equalsIgnoreCase("AM") ? amDistance : fmDistance);

            // Use the visible frequency value as the map key so commands/tab-complete can find it
            String freqName = String.valueOf(value);
            frequencies.put(freqName, new Frequency(freqName, type, distance));
        }
    }

    /**
     * Safely get a double from config with fallback.
     */
    private double getConfigDouble(org.bukkit.configuration.ConfigurationSection section, String path, double fallback) {
        if (section.contains(path) && section.get(path) instanceof Number) {
            return section.getDouble(path);
        }
        return fallback;
    }

    public Collection<Frequency> getFrequencies() {
        return frequencies.values();
    }

    public Frequency getFrequency(String name) {
        return frequencies.get(name);
    }

    /**
     * Connect a player to a frequency.
     * Thread-safe and persistent.
     */
    public boolean connect(Player player, String name, String type) {
        if (player == null || name == null || type == null) return false;
        
        Frequency freq = frequencies.get(name);
        if (freq == null || !freq.type.equalsIgnoreCase(type)) return false;
        
        // Disconnect from current frequency first
        disconnect(player);
        
        // Connect to new frequency
        freq.listeners.add(player.getUniqueId());
        playerFrequency.put(player.getUniqueId(), name);
        
        // Persist connection
        DataManager dataManager = plugin.getDataManager();
        if (dataManager != null) {
            dataManager.savePlayerFrequency(player.getUniqueId(), name);
        }
        
        return true;
    }

    /**
     * Disconnect a player from their current frequency.
     * Thread-safe and persistent.
     */
    public void disconnect(Player player) {
        if (player == null) return;
        
        UUID playerId = player.getUniqueId();
        String freqName = playerFrequency.remove(playerId);
        
        if (freqName != null) {
            Frequency freq = frequencies.get(freqName);
            if (freq != null) {
                freq.listeners.remove(playerId);
            }
            
            // Remove from persistent storage
            DataManager dataManager = plugin.getDataManager();
            if (dataManager != null) {
                dataManager.removePlayerFrequency(playerId);
            }
        }
    }

    /**
     * Get the frequency a player is currently connected to.
     */
    public Frequency getPlayerFrequency(Player player) {
        if (player == null) return null;
        
        String freqName = playerFrequency.get(player.getUniqueId());
        return freqName != null ? frequencies.get(freqName) : null;
    }
}
