package com.joshlucem.airwavechat.listeners;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.gui.GUIManager;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PasscodeListener implements Listener {
    private final GUIManager guiManager;
    private final FrequencyManager frequencyManager;
    private final AirwaveChat plugin;

    public PasscodeListener(GUIManager guiManager, FrequencyManager frequencyManager, AirwaveChat plugin) {
        this.guiManager = guiManager;
        this.frequencyManager = frequencyManager;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncChat(AsyncChatEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = event.getPlayer();
        GUIManager.PendingPasscode pending = guiManager.consumePendingPasscode(player);
        if (pending == null) return;

        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        String passcode = msg == null ? "" : msg.trim();

        FrequencyManager.Frequency freq = frequencyManager.getFrequency(pending.frequencyName);
        if (freq == null) {
            player.sendMessage(MessageUtil.color("<red>La frecuencia ya no existe."));
            event.setCancelled(true);
            return;
        }

        if (pending.forClaim) {
            freq.setEncryption(player.getUniqueId(), passcode);
            player.sendMessage(MessageUtil.color(passcode.isEmpty() ? "<yellow>Frecuencia abierta sin clave." : "<green>Clave establecida."));
            frequencyManager.authorizeEncryptedAccess(player.getUniqueId(), freq.name);
            frequencyManager.connect(player, freq.name, freq.type);
        } else {
            if (freq.checkPasscode(passcode)) {
                frequencyManager.authorizeEncryptedAccess(player.getUniqueId(), freq.name);
                frequencyManager.connect(player, freq.name, freq.type);
            } else {
                player.sendMessage(MessageUtil.color("<red>Clave incorrecta."));
            }
        }
        event.setCancelled(true);
    }
}
