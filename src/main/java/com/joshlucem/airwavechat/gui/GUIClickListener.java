package com.joshlucem.airwavechat.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

import com.joshlucem.airwavechat.AirwaveChat;
import com.joshlucem.airwavechat.manager.FrequencyManager;

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

        // Procesar clicks según el menú actual
        if (title.contains("Main Menu")) {
            event.setCancelled(true);
            handleMainMenuClick(event, player);
        } else if (title.contains("Frequencies")) {
            event.setCancelled(true);
            handleFrequenciesMenuClick(event, player);
        } else if (title.contains("Favorites")) {
            event.setCancelled(true);
            handleFavoritesMenuClick(event, player);
        } else if (title.contains("Statistics")) {
            event.setCancelled(true);
            handleStatsMenuClick(event, player);
        } else if (title.contains("Information")) {
            event.setCancelled(true);
            handleInfoMenuClick(event, player);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot < 0) return;

        var mainConfig = plugin.getGuiConfig().getConfigurationSection("main_menu");
        if (mainConfig == null) return;

        if (mainConfig.getInt("frequencies_button.slot", -1) == slot) {
            guiManager.openFrequenciesMenu(player, 0);
        } else if (mainConfig.getInt("favorites_button.slot", -1) == slot) {
            guiManager.openFavoritesMenu(player);
        } else if (mainConfig.getInt("stats_button.slot", -1) == slot) {
            guiManager.openStatsMenu(player);
        } else if (mainConfig.getInt("info_button.slot", -1) == slot) {
            guiManager.openInfoMenu(player);
        }
    }

    private void handleFrequenciesMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot < 0) return;

        var freqConfig = plugin.getGuiConfig().getConfigurationSection("frequencies_menu");
        if (freqConfig == null) return;

        // Botón Back
        if (freqConfig.getInt("back_button.slot", -1) == slot) {
            guiManager.openMainMenu(player);
            return;
        }

        // Click en frecuencia
        String frequencyName = extractFrequencyName(event.getCurrentItem());
        if (frequencyName != null && !frequencyName.isEmpty()) {
            if (event.isShiftClick()) {
                guiManager.addFavorite(player, frequencyName);
            } else {
                FrequencyManager.Frequency freq = frequencyManager.getFrequency(frequencyName);
                if (freq != null) {
                    frequencyManager.connect(player, frequencyName, freq.type);
                    player.closeInventory();
                }
            }
        }
    }

    private void handleFavoritesMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot < 0) return;

        var favConfig = plugin.getGuiConfig().getConfigurationSection("favorites_menu");
        if (favConfig == null) return;

        // Botón Back
        if (favConfig.getInt("back_button.slot", -1) == slot) {
            guiManager.openMainMenu(player);
            return;
        }

        // Click en favorito
        String frequencyName = extractFrequencyName(event.getCurrentItem());
        if (frequencyName != null && !frequencyName.isEmpty()) {
            if (event.isShiftClick()) {
                guiManager.removeFavorite(player, frequencyName);
                guiManager.openFavoritesMenu(player);
            } else {
                FrequencyManager.Frequency freq = frequencyManager.getFrequency(frequencyName);
                if (freq != null) {
                    frequencyManager.connect(player, frequencyName, freq.type);
                    player.closeInventory();
                }
            }
        }
    }

    private void handleStatsMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot < 0) return;

        var statsConfig = plugin.getGuiConfig().getConfigurationSection("stats_menu");
        if (statsConfig == null) return;

        if (statsConfig.getInt("back_button.slot", -1) == slot) {
            guiManager.openMainMenu(player);
        }
    }

    private void handleInfoMenuClick(InventoryClickEvent event, Player player) {
        int slot = event.getSlot();
        if (slot < 0) return;

        var infoConfig = plugin.getGuiConfig().getConfigurationSection("info_menu");
        if (infoConfig == null) return;

        if (infoConfig.getInt("back_button.slot", -1) == slot) {
            guiManager.openMainMenu(player);
        }
    }

    /**
     * Extrae el nombre de la frecuencia del item
     */
    private String extractFrequencyName(org.bukkit.inventory.ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        net.kyori.adventure.text.Component displayName = item.getItemMeta().displayName();
        if (displayName == null) return null;

        // Convertir Component a String plano y remover códigos de color
        String plainName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(displayName);
        return plainName.trim();
    }
}
