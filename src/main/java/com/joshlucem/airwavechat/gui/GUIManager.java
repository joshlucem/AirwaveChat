package com.joshlucem.airwavechat.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
    private final Map<UUID, Integer> playerPages;
    private final Map<UUID, String> playerLastMenu;
    private final Map<UUID, List<String>> playerFavorites;

    public GUIManager(AirwaveChat plugin, FrequencyManager frequencyManager) {
        this.plugin = plugin;
        this.frequencyManager = frequencyManager;
        this.playerPages = new HashMap<>();
        this.playerLastMenu = new HashMap<>();
        this.playerFavorites = new HashMap<>();
    }

    /**
     * Abre el menú principal al jugador
     */
    public void openMainMenu(Player player) {
        ConfigurationSection mainMenuConfig = plugin.getGuiConfig().getConfigurationSection("main_menu");
        if (mainMenuConfig == null) {
            player.sendMessage(MessageUtil.color("§cError: No main menu configuration found!"));
            return;
        }

        String titleStr = mainMenuConfig.getString("title", "Main Menu");
        net.kyori.adventure.text.Component title = MessageUtil.color(titleStr);
        int size = mainMenuConfig.getInt("size", 27);
        
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Frequencies button
        addMenuItem(menu, mainMenuConfig, "frequencies_button", "Frequencies");
        
        // Favorites button
        addMenuItem(menu, mainMenuConfig, "favorites_button", "Your Favorites");
        
        // Stats button
        addMenuItem(menu, mainMenuConfig, "stats_button", "Statistics");
        
        // Info button
        addMenuItem(menu, mainMenuConfig, "info_button", "Information");

        player.openInventory(menu);
        playerLastMenu.put(player.getUniqueId(), "main");
    }

    /**
     * Abre el menú de frecuencias
     */
    public void openFrequenciesMenu(Player player, int page) {
        ConfigurationSection freqConfig = plugin.getGuiConfig().getConfigurationSection("frequencies_menu");
        if (freqConfig == null) {
            player.sendMessage(MessageUtil.color("§cError: No frequencies menu configuration found!"));
            return;
        }

        String titleStr = freqConfig.getString("title", "Frequencies - Page " + (page + 1));
        net.kyori.adventure.text.Component title = MessageUtil.color(titleStr);
        int size = freqConfig.getInt("size", 54);
        
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Cargar frecuencias
        List<FrequencyManager.Frequency> frequencies = new java.util.ArrayList<>(frequencyManager.getFrequencies());
        int itemsPerPage = freqConfig.getInt("items_per_page", 36);
        int startIdx = page * itemsPerPage;
        int endIdx = Math.min(startIdx + itemsPerPage, frequencies.size());

        for (int i = startIdx; i < endIdx; i++) {
            FrequencyManager.Frequency freq = frequencies.get(i);
            addFrequencyItem(menu, i - startIdx, freq.name, freq.listeners.size(), freqConfig);
        }

        // Back button
        addMenuItem(menu, freqConfig, "back_button", "Back");

        player.openInventory(menu);
        playerLastMenu.put(player.getUniqueId(), "frequencies");
        playerPages.put(player.getUniqueId(), page);
    }

    /**
     * Abre el menú de favoritos
     */
    public void openFavoritesMenu(Player player) {
        ConfigurationSection favConfig = plugin.getGuiConfig().getConfigurationSection("favorites_menu");
        if (favConfig == null) {
            player.sendMessage(MessageUtil.color("§cError: No favorites menu configuration found!"));
            return;
        }

        String titleStr = favConfig.getString("title", "Your Favorites");
        net.kyori.adventure.text.Component title = MessageUtil.color(titleStr);
        int size = favConfig.getInt("size", 27);
        
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Cargar favoritos
        List<String> favorites = playerFavorites.getOrDefault(player.getUniqueId(), java.util.Collections.emptyList());
        
        int slot = 0;
        for (String favorite : favorites) {
            if (slot >= size - 3) break;
            
            FrequencyManager.Frequency freq = frequencyManager.getFrequency(favorite);
            if (freq != null) {
                addFrequencyItem(menu, slot, favorite, freq.listeners.size(), favConfig);
                slot++;
            }
        }

        // Back button
        addMenuItem(menu, favConfig, "back_button", "Back");

        player.openInventory(menu);
        playerLastMenu.put(player.getUniqueId(), "favorites");
    }

    /**
     * Abre el menú de estadísticas
     */
    public void openStatsMenu(Player player) {
        ConfigurationSection statsConfig = plugin.getGuiConfig().getConfigurationSection("stats_menu");
        if (statsConfig == null) {
            player.sendMessage(MessageUtil.color("§cError: No stats menu configuration found!"));
            return;
        }

        String titleStr = statsConfig.getString("title", "Statistics");
        net.kyori.adventure.text.Component title = MessageUtil.color(titleStr);
        int size = statsConfig.getInt("size", 27);
        
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Stats items
        int totalFreqs = frequencyManager.getFrequencies().size();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        
        addStatsItem(menu, 0, "Total Frequencies", String.valueOf(totalFreqs), statsConfig);
        addStatsItem(menu, 1, "Online Players", String.valueOf(onlinePlayers), statsConfig);

        // Back button
        addMenuItem(menu, statsConfig, "back_button", "Back");

        player.openInventory(menu);
        playerLastMenu.put(player.getUniqueId(), "stats");
    }

    /**
     * Abre el menú de información
     */
    public void openInfoMenu(Player player) {
        ConfigurationSection infoConfig = plugin.getGuiConfig().getConfigurationSection("info_menu");
        if (infoConfig == null) {
            player.sendMessage(MessageUtil.color("§cError: No info menu configuration found!"));
            return;
        }

        String titleStr = infoConfig.getString("title", "Information");
        net.kyori.adventure.text.Component title = MessageUtil.color(titleStr);
        int size = infoConfig.getInt("size", 27);
        
        Inventory menu = Bukkit.createInventory(null, size, title);

        // Info items
        addInfoItem(menu, 0, "Plugin Name", "AirwaveChat", infoConfig);
        addInfoItem(menu, 1, "Version", plugin.getPluginMeta().getVersion(), infoConfig);
        addInfoItem(menu, 2, "Developer", "joshlucem", infoConfig);

        // Back button
        addMenuItem(menu, infoConfig, "back_button", "Back");

        player.openInventory(menu);
        playerLastMenu.put(player.getUniqueId(), "info");
    }

    /**
     * Agrega un favorito para un jugador
     */
    public void addFavorite(Player player, String frequency) {
        List<String> favorites = playerFavorites.computeIfAbsent(player.getUniqueId(), k -> new java.util.ArrayList<>());
        if (!favorites.contains(frequency)) {
            favorites.add(frequency);
            player.sendMessage(MessageUtil.color("§a✓ Added " + frequency + " to favorites!"));
        }
    }

    /**
     * Remueve un favorito
     */
    public void removeFavorite(Player player, String frequency) {
        List<String> favorites = playerFavorites.get(player.getUniqueId());
        if (favorites != null && favorites.remove(frequency)) {
            player.sendMessage(MessageUtil.color("§e✗ Removed " + frequency + " from favorites."));
        }
    }

    /**
     * Obtiene el menú anterior del jugador
     */
    public String getLastMenu(Player player) {
        return playerLastMenu.getOrDefault(player.getUniqueId(), "main");
    }

    /**
     * Obtiene la página actual del jugador
     */
    public int getCurrentPage(Player player) {
        return playerPages.getOrDefault(player.getUniqueId(), 0);
    }

    // ==================== Helper Methods ====================

    private void addMenuItem(Inventory inventory, ConfigurationSection config, String key, String displayName) {
        ConfigurationSection itemConfig = config.getConfigurationSection(key);
        if (itemConfig == null) return;

        int slot = itemConfig.getInt("slot", -1);
        if (slot < 0 || slot >= inventory.getSize()) return;

        String materialName = itemConfig.getString("material", "PAPER");
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.PAPER;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.color(displayName));
            List<String> lore = itemConfig.getStringList("lore");
            if (!lore.isEmpty()) {
                java.util.List<net.kyori.adventure.text.Component> loreParsed = new java.util.ArrayList<>();
                for (String line : lore) {
                    loreParsed.add(MessageUtil.color(line));
                }
                meta.lore(loreParsed);
            }
            item.setItemMeta(meta);
        }

        inventory.setItem(slot, item);
    }

    private void addFrequencyItem(Inventory inventory, int slot, String frequency, int listeners, ConfigurationSection config) {
        String materialName = config.getString("frequency_material", "ORANGE_WOOL");
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.ORANGE_WOOL;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.color("§6" + frequency));
            java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
            lore.add(MessageUtil.color("§7Listeners: " + listeners));
            lore.add(MessageUtil.color("§bClick to connect"));
            lore.add(MessageUtil.color("§eShift-Click to favorite"));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }

    private void addStatsItem(Inventory inventory, int slot, String name, String value, ConfigurationSection config) {
        String materialName = config.getString("stats_material", "PAPER");
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.PAPER;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.color("§b" + name));
            java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
            lore.add(MessageUtil.color("§a" + value));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }

    private void addInfoItem(Inventory inventory, int slot, String name, String value, ConfigurationSection config) {
        String materialName = config.getString("info_material", "BOOK");
        Material material = Material.getMaterial(materialName);
        if (material == null) material = Material.BOOK;

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MessageUtil.color("§d" + name));
            java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
            lore.add(MessageUtil.color("§6" + value));
            meta.lore(lore);
            item.setItemMeta(meta);
        }

        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
    }
}
