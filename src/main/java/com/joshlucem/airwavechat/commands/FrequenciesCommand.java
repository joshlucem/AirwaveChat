package com.joshlucem.airwavechat.commands;

import java.util.Comparator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class FrequenciesCommand implements CommandExecutor, TabCompleter {
    private final FrequencyManager frequencyManager;
    private final AirwaveChat plugin;

    public FrequenciesCommand(FrequencyManager frequencyManager, AirwaveChat plugin) {
        this.frequencyManager = frequencyManager;
        this.plugin = plugin;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return java.util.Arrays.asList("list", "l", "current", "c", "info", "i", "top", "t", "search", "s");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l"))) {
            return java.util.Arrays.asList("AM", "FM");
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
        
        if (args.length == 0) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.usage")));
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "l": subCommand = "list"; break;
            case "c": subCommand = "current"; break;
            case "i": subCommand = "info"; break;
            case "t": subCommand = "top"; break;
            case "s": subCommand = "search"; break;
        }
        
        switch (subCommand) {
            case "list": handleList(player, args); break;
            case "current": handleCurrent(player); break;
            case "info": handleInfo(player, args); break;
            case "top": handleTop(player); break;
            case "search": handleSearch(player, args); break;
            default: player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.usage"))); break;
        }
        
        return true;
    }
    
    private void handleList(Player player, String[] args) {
        final String freqType;
        int page = 1;
        
        if (args.length >= 2) {
            String arg1 = args[1].toUpperCase();
            if (arg1.equals("AM") || arg1.equals("FM")) {
                freqType = arg1;
                if (args.length >= 3) {
                    try {
                        page = Integer.parseInt(args[2]);
                        if (page < 1) page = 1;
                    } catch (NumberFormatException e) {
                        player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.invalid_page")));
                        return;
                    }
                }
            } else {
                try {
                    page = Integer.parseInt(arg1);
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.usage")));
                    return;
                }
                freqType = null;
            }
        } else {
            freqType = null;
        }
        
        java.util.List<FrequencyManager.Frequency> frequencies = new java.util.ArrayList<>(frequencyManager.getFrequencies());
        
        if (freqType != null) {
            frequencies.removeIf(f -> !f.type.equalsIgnoreCase(freqType));
        }
        
        frequencies.sort(Comparator
            .comparingInt((FrequencyManager.Frequency f) -> f.listeners.size())
            .reversed()
            .thenComparing(f -> {
                try {
                    return Double.parseDouble(f.name);
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }));
        
        int pageSize = 10;
        int total = frequencies.size();
        int pageCount = (int) Math.ceil((double) total / pageSize);
        
        if (total == 0) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.list_empty")));
            return;
        }
        
        if (page > pageCount) page = pageCount;
        
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        String header = plugin.getMessage("frequencies.list_header");
        if (freqType != null) {
            header += plugin.getMessage("frequencies.list_filter_note").replace("{type}", freqType);
        }
        player.sendMessage(MessageUtil.color(header));
        
        for (int i = start; i < end; i++) {
            FrequencyManager.Frequency freq = frequencies.get(i);
            String row = plugin.getMessage("frequencies.list_row")
                .replace("{name}", freq.name)
                .replace("{type}", freq.type)
                .replace("{listeners}", String.valueOf(freq.listeners.size()));
            
            if (freq.listeners.size() == 0) {
                row += " " + plugin.getMessage("frequencies.no_listeners");
            }
            player.sendMessage(MessageUtil.color(row));
        }
        
        player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.list_footer")));
        String pageInfo = plugin.getMessage("frequencies.page_info")
            .replace("{page}", String.valueOf(page))
            .replace("{total_pages}", String.valueOf(pageCount));
        player.sendMessage(MessageUtil.color(pageInfo));
        
        if (page < pageCount) {
            String nextCmd = freqType != null ? 
                "/frequencies list " + freqType + " " + (page + 1) :
                "/frequencies list " + (page + 1);
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.list_next_page").replace("{command}", nextCmd)));
        }
    }
    
    private void handleCurrent(Player player) {
        FrequencyManager.Frequency freq = frequencyManager.getPlayerFrequency(player);
        if (freq == null) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.info_none")));
        } else {
            String msg = plugin.getMessage("frequencies.info_current")
                .replace("{frequency}", freq.name)
                .replace("{type}", freq.type);
            player.sendMessage(MessageUtil.color(msg));
        }
    }
    
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.info_usage")));
            return;
        }
        
        String freqName = args[1];
        FrequencyManager.Frequency freq = frequencyManager.getFrequency(freqName);
        
        if (freq == null) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.info_not_found")));
            return;
        }
        
        String msg = plugin.getMessage("frequencies.info_details")
            .replace("{frequency}", freq.name)
            .replace("{type}", freq.type)
            .replace("{listeners}", String.valueOf(freq.listeners.size()))
            .replace("{distance}", String.valueOf((int)freq.chatDistance));
        player.sendMessage(MessageUtil.color(msg));
    }
    
    private void handleTop(Player player) {
        java.util.List<FrequencyManager.Frequency> frequencies = new java.util.ArrayList<>(frequencyManager.getFrequencies());
        
        frequencies.sort((a, b) -> Integer.compare(b.listeners.size(), a.listeners.size()));
        
        player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.info_top")));
        
        int limit = Math.min(10, frequencies.size());
        for (int i = 0; i < limit; i++) {
            FrequencyManager.Frequency freq = frequencies.get(i);
            if (freq.listeners.size() == 0) break;
            
            String row = plugin.getMessage("frequencies.top_row")
                .replace("{rank}", String.valueOf(i + 1))
                .replace("{name}", freq.name)
                .replace("{type}", freq.type)
                .replace("{listeners}", String.valueOf(freq.listeners.size()));
            player.sendMessage(MessageUtil.color(row));
        }
    }
    
    private void handleSearch(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.search_usage")));
            return;
        }
        
        String query = args[1].toLowerCase();
        java.util.List<FrequencyManager.Frequency> matches = new java.util.ArrayList<>();
        
        for (FrequencyManager.Frequency freq : frequencyManager.getFrequencies()) {
            if (freq.name.toLowerCase().contains(query)) {
                matches.add(freq);
            }
        }
        
        if (matches.isEmpty()) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.search_no_results").replace("{query}", query)));
            return;
        }
        
        matches.sort((a, b) -> Integer.compare(b.listeners.size(), a.listeners.size()));
        
        player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.search_header").replace("{query}", query)));
        
        int limit = Math.min(15, matches.size());
        for (int i = 0; i < limit; i++) {
            FrequencyManager.Frequency freq = matches.get(i);
            String row = plugin.getMessage("frequencies.list_row")
                .replace("{name}", freq.name)
                .replace("{type}", freq.type)
                .replace("{listeners}", String.valueOf(freq.listeners.size()));
            player.sendMessage(MessageUtil.color(row));
        }
        
        if (matches.size() > limit) {
            player.sendMessage(MessageUtil.color(plugin.getMessage("frequencies.search_more_results").replace("{count}", String.valueOf(matches.size() - limit))));
        }
    }
}
