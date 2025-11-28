package com.joshlucem.airwavechat.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.StaticUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class SignalBarTask extends BukkitRunnable {
    private final AirwaveChat plugin;
    private final FrequencyManager frequencyManager;

    public SignalBarTask(AirwaveChat plugin, FrequencyManager frequencyManager) {
        this.plugin = plugin;
        this.frequencyManager = frequencyManager;
    }

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("options.enable_signal_bar", true)) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            FrequencyManager.Frequency freq = frequencyManager.getPlayerFrequency(player);
            if (freq == null) continue;

            // Find nearest player on same frequency
            double nearestDistance = Double.MAX_VALUE;
            for (Player other : player.getWorld().getPlayers()) {
                if (other.equals(player)) continue;
                if (!freq.listeners.contains(other.getUniqueId())) continue;

                double distance = player.getLocation().distance(other.getLocation());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                }
            }

            if (nearestDistance == Double.MAX_VALUE) {
                // No one else on frequency
                player.sendActionBar(Component.text("📻 ", NamedTextColor.GRAY)
                    .append(Component.text(freq.name + " " + freq.type, NamedTextColor.AQUA))
                    .append(Component.text(" | No signal", NamedTextColor.DARK_GRAY)));
                continue;
            }

            // Calculate signal strength
            double ratio = StaticUtil.calculateDistanceRatio(nearestDistance, freq.chatDistance);
            int bars = getSignalBars(ratio);
            TextColor color = getSignalColor(bars);

            String signalBars = getBarString(bars);
            
            player.sendActionBar(Component.text("📻 ", NamedTextColor.GRAY)
                .append(Component.text(freq.name + " " + freq.type, NamedTextColor.AQUA))
                .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                .append(Component.text(signalBars, color))
                .append(Component.text(" " + (int)nearestDistance + "m", NamedTextColor.GRAY)));
        }
    }

    private int getSignalBars(double distanceRatio) {
        if (distanceRatio >= 1.0) return 0;
        if (distanceRatio >= 0.85) return 1;
        if (distanceRatio >= 0.65) return 2;
        if (distanceRatio >= 0.40) return 3;
        if (distanceRatio >= 0.20) return 4;
        return 5;
    }

    private TextColor getSignalColor(int bars) {
        switch (bars) {
            case 5: return NamedTextColor.GREEN;
            case 4: return NamedTextColor.YELLOW;
            case 3: return NamedTextColor.GOLD;
            case 2: return NamedTextColor.RED;
            case 1: return NamedTextColor.DARK_RED;
            default: return NamedTextColor.DARK_GRAY;
        }
    }

    private String getBarString(int bars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < bars ? "▮" : "▯");
        }
        return sb.toString();
    }
}
