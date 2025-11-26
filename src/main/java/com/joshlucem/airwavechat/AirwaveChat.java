package com.joshlucem.airwavechat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.joshlucem.airwavechat.commands.ConnectCommand;
import com.joshlucem.airwavechat.commands.DisconnectCommand;
import com.joshlucem.airwavechat.listeners.FrequencyChatListener;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class AirwaveChat extends JavaPlugin {
    private static AirwaveChat instance;
    private FrequencyManager frequencyManager;
    private MessageUtil messageUtil;

    public static AirwaveChat getInstance() { return instance; }
    public FrequencyManager getFrequencyManager() { return frequencyManager; }
    public MessageUtil getMessageUtil() { return messageUtil; }

    @Override
    public void onEnable() {
            getCommand("frequencies").setExecutor(new com.joshlucem.airwavechat.commands.FrequenciesCommand());
            getCommand("frequencies").setTabCompleter(new com.joshlucem.airwavechat.commands.FrequenciesCommand());
        instance = this;
        saveDefaultConfig();
        messageUtil = new MessageUtil(this);
        frequencyManager = new FrequencyManager(this);
        getCommand("connect").setExecutor(new ConnectCommand());
        getCommand("connect").setTabCompleter(new ConnectCommand());
        getCommand("disconnect").setExecutor(new DisconnectCommand());
        getCommand("disconnect").setTabCompleter(new DisconnectCommand());
        getCommand("airwavechat").setExecutor(new com.joshlucem.airwavechat.commands.AirwaveChatCommand());
        getCommand("airwavechat").setTabCompleter(new com.joshlucem.airwavechat.commands.AirwaveChatCommand());

        Bukkit.getPluginManager().registerEvents(new FrequencyChatListener(), this);
        sendConsoleSignature();
        getLogger().info("AirwaveChat v1.0.0 currently enabled - Developed by @joshlucem");
    }

    @Override
    public void onDisable() {
    }

    private void sendConsoleSignature() {
        getServer().getConsoleSender().sendMessage("Thank you for using AirwaveChat! <3");
    }
}
