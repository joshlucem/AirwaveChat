package com.joshlucem.airwavechat.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.joshlucem.airwavechat.AirwaveChat;

public class MessageUtil {
    private static final Map<String, Object> messages = new HashMap<>();

    public MessageUtil(AirwaveChat plugin) {
        reload(plugin);
    }

    public static void reload(AirwaveChat plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        messages.clear();
        for (String key : config.getKeys(false)) {
            Object value = config.get(key);
            messages.put(key, value != null ? value : key);
        }
    }

    public static String get(String key) {
        Object value = messages.getOrDefault(key, key);
        if (value instanceof String s) return s;
        return String.valueOf(value);
    }

    public static List<String> getList(String key) {
        Object value = messages.get(key);
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object o : list) result.add(String.valueOf(o));
            return result;
        }
        return List.of(String.valueOf(value));
    }

    public static String color(String msg) {
        return msg.replaceAll("&([0-9a-fk-or])", "§$1");
    }
}
