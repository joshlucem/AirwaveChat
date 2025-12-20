package com.joshlucem.airwavechat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;
import com.joshlucem.airwavechat.util.StaticUtil;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class FrequencyChatListener implements Listener {
    private final FrequencyManager frequencyManager;
    private final com.joshlucem.airwavechat.AirwaveChat plugin;

    public FrequencyChatListener(FrequencyManager frequencyManager, com.joshlucem.airwavechat.AirwaveChat plugin) {
        this.frequencyManager = frequencyManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        FrequencyManager.Frequency freq = frequencyManager.getPlayerFrequency(sender);
        
        if (freq == null) return;

        event.setCancelled(true);

        boolean enableProximity = plugin.getConfig().getBoolean("options.enable_proximity", true);
        boolean enableStatic = plugin.getConfig().getBoolean("options.enable_static", true);
        double staticThreshold = plugin.getConfig().getDouble("options.static_threshold", 0.75);
        double staticIntensity = plugin.getConfig().getDouble("options.static_intensity", 0.5);
        int maxMessageLength = plugin.getConfig().getInt("options.max_message_length", 256);
        double maxDistance = freq.chatDistance;

        String messageText = PlainTextComponentSerializer.plainText().serialize(event.message());
        
        if (messageText.length() > maxMessageLength) {
            sender.sendMessage(MessageUtil.color(plugin.getMessage("chat.error_length")));
            messageText = messageText.substring(0, maxMessageLength);
        }
        
        for (Player receiver : sender.getWorld().getPlayers()) {
            if (!freq.listeners.contains(receiver.getUniqueId())) continue;

            if (!enableProximity || sender.equals(receiver)) {
                String rawMsg = plugin.getMessage("chat.format")
                    .replace("{frequency}", freq.name)
                    .replace("{type}", freq.type)
                    .replace("{player}", sender.getName())
                    .replace("{message}", messageText);
                receiver.sendMessage(MessageUtil.color(rawMsg));
            } else {
                var senderLoc = sender.getLocation();
                var receiverLoc = receiver.getLocation();

                if (senderLoc == null || receiverLoc == null || senderLoc.getWorld() == null || 
                    receiverLoc.getWorld() == null || !senderLoc.getWorld().equals(receiverLoc.getWorld())) {
                    continue;
                }
                
                double distance = senderLoc.distance(receiverLoc);
                
                if (distance <= maxDistance) {
                    String finalMessage = messageText;
                    
                    if (enableStatic) {
                        double distanceRatio = StaticUtil.calculateDistanceRatio(distance, maxDistance);
                        finalMessage = StaticUtil.applyStatic(messageText, distanceRatio, staticThreshold, staticIntensity);
                        
                        if (!finalMessage.equals(messageText) && plugin.getConfig().getBoolean("options.enable_sounds", true)) {
                            String soundName = plugin.getConfig().getString("options.sound_static", "entity.experience_orb.pickup");
                            try {
                                float volume = 0.3f + ((float)distanceRatio * 0.4f);
                                receiver.playSound(receiver.getLocation(), org.bukkit.Sound.valueOf(soundName.toUpperCase().replace(".", "_")), volume, 0.8f);
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    }
                    
                    String rawMsg = plugin.getMessage("chat.format")
                        .replace("{frequency}", freq.name)
                        .replace("{type}", freq.type)
                        .replace("{player}", sender.getName())
                        .replace("{message}", finalMessage);
                    receiver.sendMessage(MessageUtil.color(rawMsg));
                }
            }
        }
    }
}
