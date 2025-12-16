package com.joshlucem.airwavechat.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;
import com.joshlucem.airwavechat.util.MessageUtil;

public class GUIManager {

    private final AirwaveChat plugin;
    private final FrequencyManager frequencyManager;
    private final Map<UUID, PendingPasscode> pendingPasscodes;

    public GUIManager(AirwaveChat plugin, FrequencyManager frequencyManager) {
        this.plugin = plugin;
        this.frequencyManager = frequencyManager;
        this.pendingPasscodes = new HashMap<>();
    }

    /**
     * Menú raíz de conexión (3 líneas) con AM / FM.
     */
    public void openConnectMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.color("<aqua>Airwave Connect"));

        ItemStack fm = createItem(Material.LIME_WOOL, "<green>Conectar FM", List.of("<gray>Explora frecuencias FM"));
        ItemStack am = createItem(Material.LIGHT_BLUE_WOOL, "<aqua>Conectar AM", List.of("<gray>Explora frecuencias AM"));

        inv.setItem(11, fm);
        inv.setItem(15, am);

        player.openInventory(inv);
    }

    /**
     * Lista de canales para un tipo dado, con colores según estado.
     */
    public void openChannelList(Player player, String type) {
        Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.color("<yellow>Canales " + type));

        List<FrequencyManager.Frequency> list = new ArrayList<>();
        for (FrequencyManager.Frequency f : frequencyManager.getFrequencies()) {
            if (f.type.equalsIgnoreCase(type)) {
                list.add(f);
            }
        }

        int slot = 0;
        for (FrequencyManager.Frequency f : list) {
            if (slot >= inv.getSize()) break;
            Material mat = Material.LIME_WOOL;
            String status = "<green>Libre";
            if (f.isEncrypted()) {
                mat = Material.BLACK_WOOL;
                status = "<dark_gray>Cifrada";
            } else if (!f.listeners.isEmpty()) {
                mat = Material.ORANGE_WOOL;
                status = "<gold>En uso";
            }

            List<String> lore = new ArrayList<>();
            lore.add(status);
            lore.add("<gray>Oyentes: " + f.listeners.size());
            if (f.isEncrypted()) {
                lore.add("<red>Requiere clave");
            } else {
                lore.add("<aqua>Click para conectar");
            }

            ItemStack item = createItem(mat, "<gold>" + f.name, lore);
            inv.setItem(slot, item);
            slot++;
        }

        player.openInventory(inv);
    }

    /**
     * Menú de reclamación desde el núcleo de la estructura.
     */
    public void openClaimMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.color("<red>Reclamar Frecuencia"));
        ItemStack fm = createItem(Material.REDSTONE_BLOCK, "<gold>Crear FM", List.of("<gray>Costo: 32 bloques redstone", "<gray>8 lingotes netherite"));
        ItemStack am = createItem(Material.COPPER_BLOCK, "<yellow>Crear AM", List.of("<gray>Costo: 32 bloques redstone", "<gray>8 lingotes netherite"));
        inv.setItem(11, fm);
        inv.setItem(15, am);
        player.openInventory(inv);
    }

    public void requestPasscode(Player player, String frequencyName, boolean isClaim) {
        pendingPasscodes.put(player.getUniqueId(), new PendingPasscode(frequencyName, isClaim));
        player.sendMessage(MessageUtil.color("<yellow>Escribe la clave en el chat (o deja vacío para abierto)."));
    }

    public PendingPasscode consumePendingPasscode(Player player) {
        return pendingPasscodes.remove(player.getUniqueId());
    }

    private ItemStack createItem(Material material, String name, List<String> loreText) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.color(name));
            if (loreText != null && !loreText.isEmpty()) {
                List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
                for (String line : loreText) {
                    lore.add(MessageUtil.color(line));
                }
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static class PendingPasscode {
        public final String frequencyName;
        public final boolean forClaim;

        public PendingPasscode(String frequencyName, boolean forClaim) {
            this.frequencyName = frequencyName;
            this.forClaim = forClaim;
        }
    }
}
