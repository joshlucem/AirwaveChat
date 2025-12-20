package com.joshlucem.airwavechat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class DisconnectCommand implements CommandExecutor, TabCompleter {
    private final FrequencyManager frequencyManager;
    private final AirwaveChat plugin;
    private final java.util.HashMap<java.util.UUID, Long> cooldowns = new java.util.HashMap<>();
    private long cooldownMillis = 2000;

    public DisconnectCommand(FrequencyManager frequencyManager, AirwaveChat plugin) {
        this.frequencyManager = frequencyManager;
        this.plugin = plugin;
        int seconds = plugin.getConfig().getInt("options.cooldown_disconnect", 2);
        if (seconds < 0) seconds = 0;
        this.cooldownMillis = seconds * 1000L;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("player.not_player")));
            return true;
        }
        Player player = (Player) sender;
        com.joshlucem.airwavechat.manager.FrequencyManager.Frequency freq = frequencyManager.getPlayerFrequency(player);
        if (freq == null) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("disconnect.not_connected")));
            return true;
        }
        
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId())) {
            long last = cooldowns.get(player.getUniqueId());
            if (now - last < cooldownMillis) {
                sender.sendMessage(MessageUtil.color(plugin.getMessage("disconnect.cooldown_active")));
                return true;
            }
        }
        cooldowns.put(player.getUniqueId(), now);
        
        frequencyManager.disconnect(player);
        sender.sendMessage(MessageUtil.color(plugin.getMessage("disconnect.success")));
        
        if (plugin.getConfig().getBoolean("options.enable_sounds", true)) {
            String soundName = plugin.getConfig().getString("options.sound_disconnect", "block.note_block.bass");
            try {
                player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(soundName.toUpperCase().replace(".", "_")), 1.0f, 0.5f);
            } catch (IllegalArgumentException e) {
            }
        }
        
        return true;
    }
}
