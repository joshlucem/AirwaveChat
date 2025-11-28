package com.joshlucem.airwavechat.util;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public class AdventureProvider {
    private static BukkitAudiences audiences;

    public static BukkitAudiences get(JavaPlugin plugin) {
        if (audiences == null) {
            audiences = BukkitAudiences.create(plugin);
        }
        return audiences;
    }
}
