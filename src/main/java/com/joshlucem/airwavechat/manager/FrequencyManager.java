package com.joshlucem.airwavechat.manager;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FrequencyManager {
    private final com.joshlucem.airwavechat.AirwaveChat plugin;
    public static class Frequency {
        public final String name;
        public final String type; // AM or FM
        public final double chatDistance;
        public final Set<UUID> listeners = ConcurrentHashMap.newKeySet();
        public Frequency(String name, String type, double chatDistance) {
            this.name = name;
            this.type = type;
            this.chatDistance = chatDistance;
        }
    }
    private final Map<String, Frequency> frequencies = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerFrequency = new ConcurrentHashMap<>();

    public FrequencyManager(FileConfiguration config, com.joshlucem.airwavechat.AirwaveChat plugin) {
        if (plugin == null) throw new IllegalArgumentException("Plugin cannot be null");
        this.plugin = plugin;
        if (config == null) throw new IllegalArgumentException("Config cannot be null");
        // General config options
        boolean allowCustom = config.getBoolean("frequencies.allow_custom", true);
        double fmMin = config.getDouble("frequencies.fm.min", 100.0);
        double fmMax = config.getDouble("frequencies.fm.max", 199.9);
        double fmStep = config.getDouble("frequencies.fm.step", 0.1);
        double fmDistance = config.getDouble("frequencies.fm.chat_distance", 40.0);
        int fmMaxCount = config.getInt("frequencies.fm.max_count", 1000);
        double fmDefault = config.getDouble("frequencies.fm.default", 101.1);
        int fmGenerated = 0;
        for (double f = fmMin; f <= fmMax && fmGenerated < fmMaxCount; f += fmStep, fmGenerated++) {
            String freq = String.format("%.1f", f);
            frequencies.put(freq, new Frequency(freq, "FM", fmDistance));
        }
        int amMin = config.getInt("frequencies.am.min", 1000);
        int amMax = config.getInt("frequencies.am.max", 1999);
        int amStep = config.getInt("frequencies.am.step", 1);
        double amDistance = config.getDouble("frequencies.am.chat_distance", 100.0);
        int amMaxCount = config.getInt("frequencies.am.max_count", 1000);
        int amDefault = config.getInt("frequencies.am.default", 1000);
        int amGenerated = 0;
        for (int a = amMin; a <= amMax && amGenerated < amMaxCount; a += amStep, amGenerated++) {
            String freq = Integer.toString(a);
            frequencies.put(freq, new Frequency(freq, "AM", amDistance));
        }
        // Custom frequencies
        if (allowCustom && config.isConfigurationSection("frequencies.custom")) {
            var customSection = config.getConfigurationSection("frequencies.custom");
            if (customSection != null) {
                for (String key : customSection.getKeys(false)) {
                    String type = customSection.getString(key + ".type", "FM");
                    double value = (customSection.contains(key + ".frequency") && customSection.get(key + ".frequency") instanceof Number)
                        ? customSection.getDouble(key + ".frequency")
                        : (type.equalsIgnoreCase("AM") ? amDefault : fmDefault);
                    double distance = (customSection.contains(key + ".chat_distance") && customSection.get(key + ".chat_distance") instanceof Number)
                        ? customSection.getDouble(key + ".chat_distance")
                        : (type.equalsIgnoreCase("AM") ? amDistance : fmDistance);
                    frequencies.put(key, new Frequency(String.valueOf(value), type, distance));
                }
            }
        }

    }

    public Collection<Frequency> getFrequencies() {
        return frequencies.values();
    }
    public Frequency getFrequency(String name) {
        return frequencies.get(name);
    }
    public boolean connect(Player player, String name, String type) {
        if (player == null || name == null || type == null) return false;
        Frequency freq = frequencies.get(name);
        if (freq == null || !freq.type.equalsIgnoreCase(type)) return false;
        // Current design supports one active frequency per player.
        // If multiple are needed, refactor playerFrequency to Map<UUID, Set<String>>.
        disconnect(player);
        freq.listeners.add(player.getUniqueId());
        playerFrequency.put(player.getUniqueId(), name);
        // Save to persistent storage
        if (plugin.getDataManager() != null) {
            plugin.getDataManager().savePlayerFrequency(player.getUniqueId(), name);
        }
        return true;
    }
    public void disconnect(Player player) {
        if (player == null) return;
        String freqName = playerFrequency.remove(player.getUniqueId());
        if (freqName != null) {
            Frequency freq = frequencies.get(freqName);
            if (freq != null) freq.listeners.remove(player.getUniqueId());
            // Remove from persistent storage
            if (plugin.getDataManager() != null) {
                plugin.getDataManager().removePlayerFrequency(player.getUniqueId());
            }
        }
    }
    public Frequency getPlayerFrequency(Player player) {
        if (player == null) return null;
        String freqName = playerFrequency.get(player.getUniqueId());
        return freqName != null ? frequencies.get(freqName) : null;
    }
}
