package com.joshlucem.airwavechat.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtil {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component color(String message) {
        
        return miniMessage.deserialize(message);
    }
}
