package com.joshlucem.airwavechat.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.plugin.java.JavaPlugin;

public class FrequencyManager {
    private List<Double> validFrequencies;
    private final Map<UUID, Double> playerFrequencies = new ConcurrentHashMap<>();
    private double min;
    private double max;
    private double step;
    private final JavaPlugin plugin;

    private static final int MAX_FREQUENCIES = 100000;

    public FrequencyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        min = plugin.getConfig().getDouble("frequency.min");
        max = plugin.getConfig().getDouble("frequency.max");
        step = plugin.getConfig().getDouble("frequency.step");
        validFrequencies = generateFrequencies(min, max, step);
    }

    public void reloadFrequenciesFromConfig() {
        min = plugin.getConfig().getDouble("frequency.min");
        max = plugin.getConfig().getDouble("frequency.max");
        step = plugin.getConfig().getDouble("frequency.step");
        validFrequencies = generateFrequencies(min, max, step);
    }

    public void saveFrequencies() {
        org.bukkit.configuration.file.YamlConfiguration yaml = new org.bukkit.configuration.file.YamlConfiguration();
        for (Map.Entry<UUID, Double> entry : playerFrequencies.entrySet()) {
            yaml.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            yaml.save(new java.io.File(plugin.getDataFolder(), "frequencies.yml"));
        } catch (java.io.IOException e) {
            plugin.getLogger().log(java.util.logging.Level.WARNING, "Could not save frequencies.yml: {0}", e.getMessage());
        }
    }

    public void loadFrequencies() {
        playerFrequencies.clear();
        java.io.File file = new java.io.File(plugin.getDataFolder(), "frequencies.yml");
        if (!file.exists()) return;
        org.bukkit.configuration.file.YamlConfiguration yaml = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            try {
                UUID uuid = java.util.UUID.fromString(key);
                Double freq = yaml.getDouble(key);
                if (isValidFrequency(freq)) {
                    playerFrequencies.put(uuid, freq);
                }
            } catch (IllegalArgumentException | NullPointerException ignored) {}
        }
    }

    private List<Double> generateFrequencies(double min, double max, double step) {
        List<Double> list = new ArrayList<>();
        int count = 0;
        for (double f = min; f <= max + 1e-9; f += step) {
            list.add(Math.round(f * 10.0) / 10.0);
            count++;
            if (count >= MAX_FREQUENCIES) break;
        }
        return list;
    }

    public boolean isValidFrequency(double freq) {
        return validFrequencies.contains(Math.round(freq * 10.0) / 10.0);
    }

    public List<Double> getValidFrequencies() {
        return validFrequencies;
    }

    public boolean connectPlayer(UUID uuid, double freq) {
        if (!isValidFrequency(freq)) return false;
        Double current = playerFrequencies.get(uuid);
        if (current != null && current.equals(freq)) return false;
        playerFrequencies.put(uuid, freq);
        saveFrequencies();
        return true;
    }

    public boolean disconnectPlayer(UUID uuid) {
        boolean removed = playerFrequencies.remove(uuid) != null;
        if (removed) saveFrequencies();
        return removed;
    }

    public Double getPlayerFrequency(UUID uuid) {
        return playerFrequencies.get(uuid);
    }

    public int getFrequencyCount(double freq) {
        int count = 0;
        for (Double f : playerFrequencies.values()) {
            if (f.equals(freq)) count++;
        }
        return count;
    }

    public Set<UUID> getPlayersOnFrequency(double freq) {
        Set<UUID> set = new HashSet<>();
        for (Map.Entry<UUID, Double> e : playerFrequencies.entrySet()) {
            if (e.getValue().equals(freq)) set.add(e.getKey());
        }
        return set;
    }
}
