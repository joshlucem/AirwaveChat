package com.joshlucem.airwavechat.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.util.MessageUtil;

public class AirwaveChatCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if (sender.hasPermission("airwavechat.admin")) {
                for (String line : MessageUtil.getList("airwavechat_help_admin")) {
                    sender.sendMessage(MessageUtil.color(line));
                }
            } else {
                for (String line : MessageUtil.getList("airwavechat_help_user")) {
                    sender.sendMessage(MessageUtil.color(line));
                }
            }
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("about")) {
            sender.sendMessage("§3§m--------------------------------------");
            sender.sendMessage("§b§lAirwaveChat §7| §eRadio Chat Plugin");
            sender.sendMessage("§7Developed by §a@joshlucem");
            sender.sendMessage("§7Version: §b1.0.0");
            sender.sendMessage("§3§m--------------------------------------");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("airwavechat.admin")) {
                sender.sendMessage(MessageUtil.color(MessageUtil.get("no_permission")));
                return true;
            }
            AirwaveChat plugin = AirwaveChat.getInstance();
            plugin.reloadConfig();
            MessageUtil.reload(plugin);
            plugin.getFrequencyManager().reloadFrequenciesFromConfig();
            plugin.getFrequencyManager().loadFrequencies();
            sender.sendMessage(MessageUtil.color(MessageUtil.get("reload_success")));
            return true;
        }
        sender.sendMessage(MessageUtil.color(MessageUtil.get("usage_airwavechat")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "about", "help");
        }
        return Collections.emptyList();
    }
}
