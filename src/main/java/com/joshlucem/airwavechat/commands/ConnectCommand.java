package com.joshlucem.airwavechat.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class ConnectCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null || !(sender instanceof Player)) {
            if (sender != null) {
                String onlyPlayersMsg = MessageUtil.get("only_players");
                if (onlyPlayersMsg != null) {
                    sender.sendMessage(MessageUtil.color(onlyPlayersMsg));
                } else {
                    sender.sendMessage("Only players can use this command.");
                }
            }
            return true;
        }
        Player player = (Player) sender;
        FrequencyManager fm = AirwaveChat.getInstance().getFrequencyManager();
        if (args.length != 1) {
            player.sendMessage("&eUsage: /connect <frequency>");
            return true;
        }
        double freq;
        if (args[0].equalsIgnoreCase("random")) {
            List<Double> valid = fm.getValidFrequencies();
            if (valid.isEmpty()) {
                player.sendMessage(MessageUtil.color(MessageUtil.get("connect_out_of_range")));
                return true;
            }
            freq = valid.get(new Random().nextInt(valid.size()));
        } else {
            try {
                freq = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtil.color(MessageUtil.get("connect_invalid")));
                return true;
            }
            if (!fm.isValidFrequency(freq)) {
                player.sendMessage(MessageUtil.color(MessageUtil.get("connect_out_of_range")));
                return true;
            }
        }
        boolean changed = fm.connectPlayer(player.getUniqueId(), freq);
        if (!changed) {
            player.sendMessage(MessageUtil.color(MessageUtil.get("connect_already")));
            return true;
        }
        int count = fm.getFrequencyCount(freq);
        String msg = MessageUtil.get("connect_success");
        msg = msg.replace("{frequency}", String.valueOf(freq)).replace("{count}", String.valueOf(count));
        player.sendMessage(MessageUtil.color(msg));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        FrequencyManager fm = AirwaveChat.getInstance().getFrequencyManager();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> all = new ArrayList<>();
            all.add("random");
            for (Double f : fm.getValidFrequencies()) {
                String s = String.format(Locale.US, "%.1f", f);
                if (s.startsWith(prefix)) all.add(s);
            }
            return all;
        }
        return Collections.emptyList();
    }
}
