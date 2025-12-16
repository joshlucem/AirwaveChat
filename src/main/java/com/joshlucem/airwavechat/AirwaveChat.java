package com.joshlucem.airwavechat;

import java.io.File;
import java.io.IOException;
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
import com.joshlucem.airwavechat.gui.GUIClickListener;
import com.joshlucem.airwavechat.gui.GUIManager;
import com.joshlucem.airwavechat.listeners.FrequencyChatListener;
import com.joshlucem.airwavechat.listeners.PlayerJoinListener;
import com.joshlucem.airwavechat.listeners.PlayerQuitListener;
import com.joshlucem.airwavechat.manager.DataManager;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.tasks.SignalBarTask;

public class AirwaveChat extends JavaPlugin {

    private FrequencyManager frequencyManager;
    private DataManager dataManager;
    private GUIManager guiManager;
    private FileConfiguration config;
    private FileConfiguration guiConfig;
    private FileConfiguration messages;
    private Map<String, String> msg;
    private int signalTaskId = -1;

    /**
     * Get a single message string by key, e.g. "connect.success" or
     * "help.header". Optimized with caching to reduce lookups.
     */
    public String getMessage(String key) {
        return msg != null ? msg.getOrDefault(key, "Message not found: " + key) : "Message not loaded: " + key;
    }

    /**
     * Get a list of messages by key, e.g. "help.menu_user" or
     * "help.menu_admin".
     */
    public java.util.List<String> getMessageList(String key) {
        if (messages != null && messages.contains("messages." + key)) {
            return messages.getStringList("messages." + key);
        }
        return java.util.Collections.singletonList("Message list not found: " + key);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    @Override
    public void onEnable() {
        getLogger().info("AirwaveChat v1.0.3 enabled. Developed by joshlucem.");
        reloadConfigFiles();
        initializeComponents();
        startSignalTask();
    }

    /**
     * Reload configuration and rebuild managers, listeners, commands and tasks safely.
     */
    public void reloadPluginComponents() {
        reloadConfigFiles();
        HandlerList.unregisterAll(this);
        stopSignalTask();
        initializeComponents();
        startSignalTask();
    }

    /**
     * Register all commands with their executors and tab completers.
     */
    private void registerCommands() {
        registerCommand("connect", new ConnectCommand(frequencyManager, this));
        registerCommand("disconnect", new DisconnectCommand(frequencyManager, this));
        registerCommand("frequencies", new FrequenciesCommand(frequencyManager, this));

        var airwaveCmd = getCommand("airwavechat");
        if (airwaveCmd != null) {
            airwaveCmd.setExecutor(new AirwaveChatCommand(this));
        }
    }

    /**
     * Helper to register a command with executor and tab completer.
     */
    private void registerCommand(String name, Object handler) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor((org.bukkit.command.CommandExecutor) handler);
            if (handler instanceof org.bukkit.command.TabCompleter) {
                cmd.setTabCompleter((org.bukkit.command.TabCompleter) handler);
            }
        }
    }

    /**
     * Register all event listeners.
     */
    private void registerListeners() {
        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new FrequencyChatListener(frequencyManager, this), this);
        pluginManager.registerEvents(new PlayerQuitListener(frequencyManager), this);
        pluginManager.registerEvents(new PlayerJoinListener(frequencyManager, dataManager), this);
        pluginManager.registerEvents(new GUIClickListener(this, guiManager, frequencyManager), this);
    }

    public void reloadConfigFiles() {
        saveDefaultConfig();

        // Load main config
        File configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        // Load GUI config
        File guiFile = new File(getDataFolder(), "gui.yml");
        if (!guiFile.exists()) {
            saveResource("gui.yml", false);
        }
        guiConfig = YamlConfiguration.loadConfiguration(guiFile);

        // Load messages from appropriate language file
        String language = config.getString("options.language", "en").toLowerCase();
        loadLanguageMessages(language);

    }

    /**
     * Load messages from the specified language file and import into
     * messages.yml
     */
    private void loadLanguageMessages(String language) {
        File messagesFile = new File(getDataFolder(), "messages.yml");

        // Map language codes to file names
        String languageFileName = switch (language) {
            case "es" ->
                "messages_es.yml";
            case "pt" ->
                "messages_pt.yml";
            default ->
                "messages_en.yml";
        };

        // Load language file
        File languageFile = new File(getDataFolder(), languageFileName);
        if (!languageFile.exists()) {
            saveResource(languageFileName, false);
        }

        FileConfiguration languageConfig = YamlConfiguration.loadConfiguration(languageFile);

        // Copy language messages to messages.yml
        if (languageConfig.contains("messages")) {
            try {
                messages = YamlConfiguration.loadConfiguration(messagesFile);

                // Get language messages section
                var langSection = languageConfig.getConfigurationSection("messages");
                if (langSection != null) {
                    // Clear existing messages
                    if (messages.contains("messages")) {
                        messages.set("messages", null);
                    }

                    // Copy all language messages to main file
                    copyConfigurationSection(langSection, messages, "messages");

                    // Save updated messages file
                    messages.save(messagesFile);

                    getLogger().info("Loaded messages from language: " + language);
                }
            } catch (IOException e) {
                getLogger().severe("Failed to save messages.yml: " + e.getMessage());
            }
        }

        // Load messages into cache
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        msg = new HashMap<>();
        if (messages != null && messages.contains("messages")) {
            var section = messages.getConfigurationSection("messages");
            if (section != null) {
                loadMessagesRecursive(section, "");
            }
        }
    }

    /**
     * Copy configuration section from source to destination.
     */
    private void copyConfigurationSection(org.bukkit.configuration.ConfigurationSection source,
            FileConfiguration destination,
            String path) {
        for (String key : source.getKeys(false)) {
            String fullPath = path.isEmpty() ? key : path + "." + key;
            Object value = source.get(key);

            if (value instanceof org.bukkit.configuration.ConfigurationSection) {
                destination.createSection(fullPath);
                copyConfigurationSection((org.bukkit.configuration.ConfigurationSection) value, destination, fullPath);
            } else {
                destination.set(fullPath, value);
            }
        }
    }

    /**
     * Recursively load messages into cache map for fast access.
     */
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
        getLogger().info("AirwaveChat v1.0.3 disabled. See you next time!");
    }

    private void initializeComponents() {
        frequencyManager = new FrequencyManager(config, this);
        dataManager = new DataManager(this);
        guiManager = new GUIManager(this, frequencyManager);

        // Register commands and listeners again to point to the new managers
        registerCommands();
        registerListeners();
    }

    private void startSignalTask() {
        if (!config.getBoolean("options.enable_signal_bar", true)) {
            return;
        }
        
        // Check if running on Folia (which uses a different scheduler API)
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            // Running on Folia - use async scheduler with TimeUnit
            int signalInterval = config.getInt("options.signal_update_interval", 20);
            long intervalMillis = signalInterval * 50L; // Convert ticks to milliseconds
            
            org.bukkit.Bukkit.getAsyncScheduler().runAtFixedRate(this, 
                task -> new SignalBarTask(this, frequencyManager).run(), 
                1000L, intervalMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
            getLogger().info("Signal bar task started using Folia async scheduler");
        } catch (ClassNotFoundException e) {
            // Not Folia - use traditional scheduler (Paper/Spigot)
            int signalInterval = config.getInt("options.signal_update_interval", 20);
            signalTaskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, 
                new SignalBarTask(this, frequencyManager), 20L, signalInterval);
        }
    }

    private void stopSignalTask() {
        // Check if Folia
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            // Folia - tasks are cancelled automatically on plugin disable
            // Individual task cancellation not needed with async scheduler
        } catch (ClassNotFoundException e) {
            // Not Folia - use traditional cancellation
            if (signalTaskId != -1) {
                getServer().getScheduler().cancelTask(signalTaskId);
                signalTaskId = -1;
            }
        }
    }
}
