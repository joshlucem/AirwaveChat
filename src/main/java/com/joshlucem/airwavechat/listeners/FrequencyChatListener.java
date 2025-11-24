package com.joshlucem.airwavechat.listeners;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class FrequencyChatListener implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        FrequencyManager fm = AirwaveChat.getInstance().getFrequencyManager();
        Double freq = fm.getPlayerFrequency(sender.getUniqueId());
        if (freq == null) return;
        event.setCancelled(true);
        Set<UUID> recipients = fm.getPlayersOnFrequency(freq);
        for (UUID uuid : recipients) {
            Player p = sender.getServer().getPlayer(uuid);
            if (p != null && p.isOnline()) {
                String msg = MessageUtil.get("chat_format");
                if (msg == null) msg = "&7[{frequency}] &f{player}: &r{message}";
                msg = msg.replace("{frequency}", String.valueOf(freq))
                         .replace("{player}", sender.getName())
                         .replace("{message}", event.getMessage());
                p.sendMessage(MessageUtil.color(msg));
            }
        }
    }
}
