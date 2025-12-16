package com.joshlucem.airwavechat.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class GUIClickListener implements Listener {

    private final AirwaveChat plugin;
    private final GUIManager guiManager;
    private final FrequencyManager frequencyManager;

    public GUIClickListener(AirwaveChat plugin, GUIManager guiManager, FrequencyManager frequencyManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.frequencyManager = frequencyManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();
        String title = view.getTitle();

        if (title.contains("Airwave Connect")) {
            event.setCancelled(true);
            handleRoot(event, player);
        } else if (title.contains("Canales")) {
            event.setCancelled(true);
            handleChannelList(event, player);
        } else if (title.contains("Reclamar Frecuencia")) {
            event.setCancelled(true);
            handleClaimMenu(event, player);
        }
    }

    private void handleRoot(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot == 11) {
            guiManager.openChannelList(player, "FM");
        } else if (slot == 15) {
            guiManager.openChannelList(player, "AM");
        }
    }

    private void handleChannelList(InventoryClickEvent event, Player player) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String name = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
            .serialize(clicked.getItemMeta().displayName());
        if (name == null || name.isEmpty()) return;

        FrequencyManager.Frequency freq = frequencyManager.getFrequency(name);
        if (freq == null) return;

        if (freq.isEncrypted()) {
            guiManager.requestPasscode(player, freq.name, false);
            player.closeInventory();
            player.sendMessage(MessageUtil.color("<red>Frecuencia cifrada. Ingresa la clave en el chat."));
        } else {
            frequencyManager.connect(player, freq.name, freq.type);
            player.closeInventory();
        }
    }

    private void handleClaimMenu(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot != 11 && slot != 15) return;
        String type = slot == 11 ? "FM" : "AM";
        // Validar costo
        if (!hasCost(player)) {
            player.sendMessage(MessageUtil.color("<red>Necesitas 32 bloques de redstone y 8 lingotes de netherite."));
            return;
        }
        takeCost(player);
        // Crear frecuencia nueva
        double distance = type.equals("FM") ? plugin.getConfig().getDouble("frequencies.fm.chat_distance", 40.0)
            : plugin.getConfig().getDouble("frequencies.am.chat_distance", 100.0);
        String freqName = generateName(type);
        frequencyManager.createCustomFrequency(freqName, type, distance, player.getUniqueId(), "");
        player.closeInventory();
        player.sendMessage(MessageUtil.color("<green>Frecuencia " + freqName + " creada. Escribe clave en el chat (o deja vacío)."));
        guiManager.requestPasscode(player, freqName, true);
    }

    private boolean hasCost(Player player) {
        return player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE_BLOCK), 32)
            && player.getInventory().containsAtLeast(new ItemStack(Material.NETHERITE_INGOT), 8);
    }

    private void takeCost(Player player) {
        player.getInventory().removeItem(new ItemStack(Material.REDSTONE_BLOCK, 32));
        player.getInventory().removeItem(new ItemStack(Material.NETHERITE_INGOT, 8));
    }

    private String generateName(String type) {
        String base = type + "-" + (int)(Math.random() * 9000 + 1000);
        while (frequencyManager.getFrequency(base) != null) {
            base = type + "-" + (int)(Math.random() * 9000 + 1000);
        }
        return base;
    }
}
