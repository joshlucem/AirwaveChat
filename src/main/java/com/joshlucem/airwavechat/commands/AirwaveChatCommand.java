package com.joshlucem.airwavechat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.util.MessageUtil;

public class AirwaveChatCommand implements CommandExecutor, TabCompleter {
    private final AirwaveChat plugin;
    public AirwaveChatCommand(AirwaveChat plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("help.header")));
            if (sender.hasPermission("airwavechat.admin")) {
                sender.sendMessage(MessageUtil.color(plugin.getMessage("help.admin_header")));
                for (String line : plugin.getMessageList("help.menu_admin")) {
                    sender.sendMessage(MessageUtil.color(line));
                }
            }
            sender.sendMessage(MessageUtil.color(plugin.getMessage("help.user_header")));
            for (String line : plugin.getMessageList("help.menu_user")) {
                sender.sendMessage(MessageUtil.color(line));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("airwavechat.admin")) {
                sender.sendMessage(MessageUtil.color(plugin.getMessage("permission.no_permission")));
                return true;
            }
            try {
                plugin.reloadConfigFiles();
                sender.sendMessage(MessageUtil.color(plugin.getMessage("reload.success")));
            } catch (Exception e) {
                sender.sendMessage(MessageUtil.color(plugin.getMessage("reload.fail")));
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("about")) {
            sender.sendMessage(MessageUtil.color(
                "<aqua>AirwaveChat by @joshlucem. Version: " + 
                plugin.getPluginMeta().getVersion() + 
                "</aqua>"));           
                 return true;
        }
        sender.sendMessage(MessageUtil.color(plugin.getMessage("error.unknown_command")));
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            java.util.List<String> subs = new java.util.ArrayList<>();
            subs.add("help");
            if (sender.hasPermission("airwavechat.admin")) subs.add("reload");
            subs.add("about");
            return subs;
        }
        return java.util.Collections.emptyList();
    }
}
