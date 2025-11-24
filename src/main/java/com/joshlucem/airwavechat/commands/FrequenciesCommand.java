package com.joshlucem.airwavechat.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class FrequenciesCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                if (args[0].equalsIgnoreCase("top")) {
                    FrequencyManager fmgr = AirwaveChat.getInstance().getFrequencyManager();
                    Map<Double, Integer> freqCounts = new HashMap<>();
                    for (Double freq : fmgr.getValidFrequencies()) {
                        freqCounts.put(freq, fmgr.getFrequencyCount(freq));
                    }
                    List<Map.Entry<Double, Integer>> sorted = new ArrayList<>(freqCounts.entrySet());
                    sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
                    int maxShow = 10;
                    StringBuilder sb = new StringBuilder(MessageUtil.color(MessageUtil.get("frequencies_top_header")));
                    int shown = 0;
                    for (Map.Entry<Double, Integer> entry : sorted) {
                        if (entry.getValue() > 0) {
                            sb.append(String.format("\n&b%.1f &7- &e%d user(s)", entry.getKey(), entry.getValue()));
                            shown++;
                            if (shown >= maxShow) break;
                        }
                    }
                    if (shown == 0) sb.append("\n").append(MessageUtil.color(MessageUtil.get("frequencies_top_none")));
                    sender.sendMessage(sb.toString());
                    return true;
                }
        FrequencyManager fm = AirwaveChat.getInstance().getFrequencyManager();
        if (args.length == 0) {
            int total = fm.getValidFrequencies().size();
            String msg = MessageUtil.get("frequencies_total");
            msg = msg.replace("{total}", String.valueOf(total));
            sender.sendMessage(MessageUtil.color(msg));
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            List<Double> freqs = fm.getValidFrequencies();
            int perPage = 50;
            int page = 1;
            if (args.length == 2) {
                try {
                    page = Integer.parseInt(args[1]);
                    if (page < 1) {
                        sender.sendMessage(MessageUtil.color(MessageUtil.get("frequencies_page_invalid")));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.color(MessageUtil.get("frequencies_page_nan")));
                    return true;
                }
            }
            int totalPages = (int) Math.ceil((double)freqs.size() / perPage);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) {
                String msg = MessageUtil.get("frequencies_page_too_high");
                msg = msg.replace("{max}", String.valueOf(totalPages));
                sender.sendMessage(MessageUtil.color(msg));
                return true;
            }
            int start = (page - 1) * perPage;
            int end = Math.min(start + perPage, freqs.size());
            StringBuilder sb = new StringBuilder(MessageUtil.color(MessageUtil.get("frequencies_list_header")));
            sb.append(String.format(" &7(Page %d/%d)\n", page, totalPages));
            for (int i = start; i < end; i++) {
                sb.append(String.format(" %.1f", freqs.get(i)));
            }
            if (freqs.isEmpty()) {
                sb.append(MessageUtil.color(MessageUtil.get("frequencies_none_available")));
            }
            sender.sendMessage(sb.toString());
            return true;
        }
        if (args[0].equalsIgnoreCase("current")) {
            if (sender == null || !(sender instanceof org.bukkit.entity.Player)) {
                if (sender != null) {
                    String onlyPlayersMsg = MessageUtil.get("only_players");
                    if (onlyPlayersMsg != null) {
                        String coloredMsg = MessageUtil.color(onlyPlayersMsg);
                        if (coloredMsg != null) {
                            sender.sendMessage(coloredMsg);
                        } else {
                            sender.sendMessage("Only players can use this command.");
                        }
                    } else {
                        sender.sendMessage("Only players can use this command.");
                    }
                }
                return true;
            }
            org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
            Double freq = fm.getPlayerFrequency(player.getUniqueId());
            if (freq == null) {
                sender.sendMessage(MessageUtil.color(MessageUtil.get("frequencies_none")));
                return true;
            }
            int count = fm.getFrequencyCount(freq);
            String msg = MessageUtil.get("frequencies_current");
            msg = msg.replace("{frequency}", String.valueOf(freq)).replace("{count}", String.valueOf(count));
            sender.sendMessage(MessageUtil.color(msg));
            return true;
        }
        if (args[0].equalsIgnoreCase("info") && args.length == 2) {
            try {
                double freq = Double.parseDouble(args[1]);
                if (!fm.isValidFrequency(freq)) {
                    sender.sendMessage(MessageUtil.color(MessageUtil.get("connect_out_of_range")));
                    return true;
                }
                int count = fm.getFrequencyCount(freq);
                String msg = MessageUtil.get("frequencies_info");
                msg = msg.replace("{frequency}", String.valueOf(freq)).replace("{count}", String.valueOf(count));
                sender.sendMessage(MessageUtil.color(msg));
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.color(MessageUtil.get("connect_invalid")));
            }
            return true;
        }
        sender.sendMessage(MessageUtil.color(MessageUtil.get("frequencies_usage")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        FrequencyManager fm = AirwaveChat.getInstance().getFrequencyManager();
        if (args.length == 1) {
            return Arrays.asList("list", "current", "info", "top");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            List<String> all = new ArrayList<>();
            for (Double f : fm.getValidFrequencies()) {
                all.add(String.format(java.util.Locale.US, "%.1f", f));
            }
            return all;
        }
        return Collections.emptyList();
    }
}
