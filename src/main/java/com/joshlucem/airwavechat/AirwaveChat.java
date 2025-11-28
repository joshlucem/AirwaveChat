package com.joshlucem.airwavechat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.joshlucem.airwavechat.commands.AirwaveChatCommand;
import com.joshlucem.airwavechat.commands.ConnectCommand;
import com.joshlucem.airwavechat.commands.DisconnectCommand;
import com.joshlucem.airwavechat.commands.FrequenciesCommand;
import com.joshlucem.airwavechat.listeners.FrequencyChatListener;
import com.joshlucem.airwavechat.manager.FrequencyManager;

public class AirwaveChat extends JavaPlugin {
    private FrequencyManager frequencyManager;
    private com.joshlucem.airwavechat.manager.DataManager dataManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private Map<String, String> msg;

    /**
     * Get a single message string by key, e.g. "connect.success" or "help.header".
     */
    public String getMessage(String key) {
        return msg != null ? msg.getOrDefault(key, "Message not found: " + key) : "Message not loaded: " + key;
    }

    /**
     * Get a list of messages by key, e.g. "help.menu_user" or "help.menu_admin".
     */
    public java.util.List<String> getMessageList(String key) {
        if (messages != null && messages.contains("messages." + key)) {
            return messages.getStringList("messages." + key);
        }
        return java.util.Collections.singletonList("Message list not found: " + key);
    }

    public com.joshlucem.airwavechat.manager.DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onEnable() {
        getLogger().info("AirwaveChat enabled. Developed by joshlucem.");
        reloadConfigFiles();

        frequencyManager = new FrequencyManager(config, this);
        dataManager = new com.joshlucem.airwavechat.manager.DataManager(this);

        if (getCommand("connect") != null) {
            ConnectCommand connect = new ConnectCommand(frequencyManager, this);
            getCommand("connect").setExecutor(connect);
            getCommand("connect").setTabCompleter(connect);
        }
        if (getCommand("disconnect") != null) {
            DisconnectCommand disconnect = new DisconnectCommand(frequencyManager, this);
            getCommand("disconnect").setExecutor(disconnect);
            getCommand("disconnect").setTabCompleter(disconnect);
        }
        if (getCommand("frequencies") != null) {
            FrequenciesCommand frequencies = new FrequenciesCommand(frequencyManager, this);
            getCommand("frequencies").setExecutor(frequencies);
            getCommand("frequencies").setTabCompleter(frequencies);
        }
        if (getCommand("airwavechat") != null) {
            getCommand("airwavechat").setExecutor(new AirwaveChatCommand(this));
        }

        getServer().getPluginManager().registerEvents(new FrequencyChatListener(frequencyManager, this), this);
        getServer().getPluginManager().registerEvents(new com.joshlucem.airwavechat.listeners.PlayerQuitListener(frequencyManager), this);
        getServer().getPluginManager().registerEvents(new com.joshlucem.airwavechat.listeners.PlayerJoinListener(frequencyManager, dataManager), this);
        
        // Start signal bar task
        int signalInterval = config.getInt("options.signal_update_interval", 20);
        new com.joshlucem.airwavechat.tasks.SignalBarTask(this, frequencyManager).runTaskTimer(this, 20L, signalInterval);
    }

    public void reloadConfigFiles() {
        saveDefaultConfig();
        // Copiar messages.yml si no existe
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        msg = new HashMap<>();
        if (messages != null && messages.contains("messages")) {
            var section = messages.getConfigurationSection("messages");
            if (section != null) {
                loadMessagesRecursive(section, "");
            }
        }

        // Recargar FrequencyManager solo si ya existe (para reload)
        if (frequencyManager != null) {
            frequencyManager = new FrequencyManager(config, this);
        }
    }

    // Carga recursiva de claves anidadas en el HashMap msg
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
        getLogger().info("AirwaveChat disabled. See you next time!");
    }
}
