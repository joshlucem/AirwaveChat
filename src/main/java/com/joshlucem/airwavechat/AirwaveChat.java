package com.joshlucem.airwavechat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.joshlucem.airwavechat.commands.AirwaveChatCommand;
import com.joshlucem.airwavechat.commands.ConnectCommand;
import com.joshlucem.airwavechat.commands.DisconnectCommand;
import com.joshlucem.airwavechat.commands.FrequenciesCommand;
import com.joshlucem.airwavechat.listeners.FrequencyChatListener;
import com.joshlucem.airwavechat.listeners.PlayerJoinListener;
import com.joshlucem.airwavechat.listeners.PlayerQuitListener;
import com.joshlucem.airwavechat.manager.DataManager;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.tasks.SignalBarTask;

public class AirwaveChat extends JavaPlugin {

    private FrequencyManager frequencyManager;
    private DataManager dataManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private Map<String, String> msg;
    private int signalTaskId = -1;

    public String getMessage(String key) {
        if (msg == null) {
            return "Messages not loaded: " + key;
        }
        return msg.getOrDefault(key, "Message not found: " + key);
    }

    public java.util.List<String> getMessageList(String key) {
        if (messages != null && messages.contains("messages." + key)) {
            return messages.getStringList("messages." + key);
        }
        return java.util.Collections.singletonList(getMessage("error.list_not_found").replace("{key}", key));
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onEnable() {
        reloadConfigFiles();
        getLogger().info("Plugin enabled. Proximity radio chat system ready.");
        initializeComponents();
        startSignalTask();
    }

    public void reloadPluginComponents() {
        reloadConfigFiles();
        HandlerList.unregisterAll(this);
        stopSignalTask();
        initializeComponents();
        startSignalTask();
    }

    private void registerCommands() {
        registerCommand("connect", new ConnectCommand(frequencyManager, this));
        registerCommand("disconnect", new DisconnectCommand(frequencyManager, this));
        registerCommand("frequencies", new FrequenciesCommand(frequencyManager, this));

        var airwaveCmd = getCommand("airwavechat");
        if (airwaveCmd != null) {
            airwaveCmd.setExecutor(new AirwaveChatCommand(this));
        }
    }

    private void registerCommand(String name, Object handler) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor((org.bukkit.command.CommandExecutor) handler);
            if (handler instanceof org.bukkit.command.TabCompleter) {
                cmd.setTabCompleter((org.bukkit.command.TabCompleter) handler);
            }
        }
    }

    private void registerListeners() {
        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new FrequencyChatListener(frequencyManager, this), this);
        pluginManager.registerEvents(new PlayerQuitListener(frequencyManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(frequencyManager, dataManager), this);
    }

    public void reloadConfigFiles() {
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        loadMessages();
    }

    private void loadMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        msg = new HashMap<>();
        if (messages != null && messages.contains("messages")) {
            var section = messages.getConfigurationSection("messages");
            if (section != null) {
                loadMessagesRecursive(section, "");
            }
        }
    }

    private void loadMessagesRecursive(org.bukkit.configuration.ConfigurationSection section, String prefix) {
        for (String key : section.getKeys(false)) {
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            Object value = section.get(key);
            if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                loadMessagesRecursive((org.bukkit.configuration.ConfigurationSection) value, fullKey);
            } else if (value instanceof String) {
                msg.put(fullKey, (String) value);
            }
        }
    }

    @Override
    public void onDisable() {
        stopSignalTask();
        if (frequencyManager != null) {
            for (org.bukkit.entity.Player player : getServer().getOnlinePlayers()) {
                frequencyManager.disconnect(player);
            }
        }
        getLogger().info("Plugin disabled.");
    }

    private void initializeComponents() {
        frequencyManager = new FrequencyManager(config, this);
        dataManager = new DataManager(this);
        registerCommands();
        registerListeners();
    }

    private void startSignalTask() {
        if (!config.getBoolean("options.enable_signal_bar", true)) {
            return;
        }
        
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            int signalInterval = config.getInt("options.signal_update_interval", 20);
            long intervalMillis = signalInterval * 50L;
            
            org.bukkit.Bukkit.getAsyncScheduler().runAtFixedRate(this, 
                task -> new SignalBarTask(this, frequencyManager).run(), 
                1000L, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
            getLogger().info("Signal bar task started (Folia async scheduler)");
        } catch (ClassNotFoundException e) {
            int signalInterval = config.getInt("options.signal_update_interval", 20);
            signalTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, 
                new SignalBarTask(this, frequencyManager), 20L, signalInterval);
        }
    }

    private void stopSignalTask() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
        } catch (ClassNotFoundException e) {
            if (signalTaskId != -1) {
                getServer().getScheduler().cancelTask(signalTaskId);
                signalTaskId = -1;
            }
        }
    }
}
