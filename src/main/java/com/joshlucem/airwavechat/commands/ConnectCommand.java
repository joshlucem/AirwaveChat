package com.joshlucem.airwavechat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class ConnectCommand implements CommandExecutor, TabCompleter {
    private final FrequencyManager frequencyManager;
    private final AirwaveChat plugin;
    private final java.util.HashMap<java.util.UUID, Long> cooldowns = new java.util.HashMap<>();
    private long COOLDOWN_MILLIS = 5000; // fallback

    public ConnectCommand(FrequencyManager frequencyManager, AirwaveChat plugin) {
        this.frequencyManager = frequencyManager;
        this.plugin = plugin;
        // Read cooldown from config options.cooldown_connect (seconds)
        int seconds = plugin.getConfig().getInt("options.cooldown_connect", 5);
        if (seconds < 0) seconds = 0;
        this.COOLDOWN_MILLIS = seconds * 1000L;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return java.util.Arrays.asList("FM", "AM");
        }
        if (args.length == 2) {
            String type = args[0].toUpperCase();
            java.util.List<String> matches = new java.util.ArrayList<>();
            for (com.joshlucem.airwavechat.manager.FrequencyManager.Frequency freq : frequencyManager.getFrequencies()) {
                if (freq.type.equalsIgnoreCase(type)) {
                    matches.add(freq.name);
                }
            }
            // Limit to 20 suggestions for performance
            if (matches.size() > 20) {
                return matches.subList(0, 20);
            }
            return matches;
        }
        return java.util.Collections.emptyList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("player.not_player")));
            return true;
        }
        Player player = (Player) sender;
        // Open GUI when no arguments are provided
        if (args.length < 2) {
            plugin.getGUIManager().openConnectMenu(player);
            return true;
        }

        String type = args[0].toUpperCase();
        String frequency = args[1];
        if (!type.equals("AM") && !type.equals("FM")) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("connect.invalid_type")));
            return true;
        }

        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId())) {
            long last = cooldowns.get(player.getUniqueId());
            if (now - last < COOLDOWN_MILLIS) {
                sender.sendMessage(MessageUtil.color(plugin.getMessage("connect.cooldown_active")));
                return true;
            }
        }
        cooldowns.put(player.getUniqueId(), now);
        boolean success = frequencyManager.connect(player, frequency, type);
        if (success) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("connect.success").replace("{frequency}", frequency).replace("{type}", type)));
            // Play connect sound
            if (plugin.getConfig().getBoolean("options.enable_sounds", true)) {
                String soundName = plugin.getConfig().getString("options.sound_connect", "block.note_block.pling");
                try {
                    player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(soundName.toUpperCase().replace(".", "_")), 1.0f, 1.0f);
                } catch (IllegalArgumentException e) {
                    // Invalid sound, skip
                }
            }
        } else {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("connect.not_found").replace("{frequency}", frequency).replace("{type}", type)));
        }
        return true;
    }
}
