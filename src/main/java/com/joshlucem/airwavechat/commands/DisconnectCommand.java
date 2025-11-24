package com.joshlucem.airwavechat.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class DisconnectCommand implements CommandExecutor, TabCompleter {
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
        boolean wasConnected = fm.disconnectPlayer(player.getUniqueId());
        if (!wasConnected) {
            player.sendMessage(MessageUtil.color(MessageUtil.get("disconnect_not_connected")));
            return true;
        }
        player.sendMessage(MessageUtil.color(MessageUtil.get("disconnect_success")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
