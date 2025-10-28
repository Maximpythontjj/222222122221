package com.example.enderchestec.gui;

import com.example.enderchestec.EnderChestEC;
import com.example.enderchestec.storage.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GUI для расширяемого эндер-сундука
 */
public class EnderChestGUI implements Listener {
    
    private final EnderChestEC plugin;
    private final PlayerDataManager dataManager;
    
    // Кэш открытых GUI для игроков
    private final Map<UUID, Inventory> openGUIs = new ConcurrentHashMap<>();
    
    public EnderChestGUI(EnderChestEC plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getPlayerDataManager();
        
        // Регистрируем обработчики событий
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Открыть GUI эндер-сундука для игрока
     */
    public void openEnderChest(Player player) {
        UUID playerUUID = player.getUniqueId();
        
        // Инициализируем игрока если нужно
        dataManager.initializePlayer(playerUUID);
        
        int playerSlots = dataManager.getPlayerSlots(playerUUID);
        int maxSlots = plugin.getMaxSlots();
        
        // Вычисляем размер инвентаря (должен быть кратен 9, максимум 54)
        int inventorySize = Math.min(((maxSlots - 1) / 9 + 1) * 9, 54);
        
        // Создаем инвентарь
        Inventory inventory = Bukkit.createInventory(null, inventorySize, 
            "§6§lРасширяемый Эндер-сундук §7(" + playerSlots + "/" + maxSlots + ")");
        
        // Загружаем предметы игрока
        Map<Integer, ItemStack> virtualInventory = dataManager.getVirtualInventory(playerUUID);
        for (int i = 0; i < inventorySize; i++) {
            if (i < playerSlots) {
                // Доступный слот
                ItemStack item = virtualInventory.get(i);
                if (item != null) {
                    inventory.setItem(i, item);
                }
            } else {
                // Заблокированный слот
                inventory.setItem(i, createLockedSlot());
            }
        }
        
        // Сохраняем ссылку на GUI
        openGUIs.put(playerUUID, inventory);
        
        // Открываем инвентарь
        player.openInventory(inventory);
    }
    
    /**
     * Создать предмет для заблокированного слота
     */
    private ItemStack createLockedSlot() {
        ItemStack lockedSlot = new ItemStack(Material.BARRIER);
        ItemMeta meta = lockedSlot.getItemMeta();
        
        if (meta != null) {
            String name = plugin.getMessage("locked-slot-name");
            meta.setDisplayName(name.replace("&", "§"));
            
            String costString = String.valueOf(plugin.getCostPerSlot());
            
            // Получаем лор из конфига
            java.util.List<String> lore = plugin.getConfig().getStringList("messages.locked-slot-lore");
            if (lore != null && !lore.isEmpty()) {
                java.util.List<String> coloredLore = new java.util.ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(line.replace("&", "§").replace("%cost%", costString));
                }
                meta.setLore(coloredLore);
            } else {
                // Лор по умолчанию если не найден в конфиге
                meta.setLore(Arrays.asList(
                    "§7Нажмите, чтобы разблокировать",
                    "§7Стоимость: §e" + costString + " алмазов"
                ));
            }
            
            lockedSlot.setItemMeta(meta);
        }
        
        return lockedSlot;
    }
    
    /**
     * Обработка клика по инвентарю
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        
        // Проверяем, что это наш GUI
        if (!openGUIs.containsKey(playerUUID) || !openGUIs.get(playerUUID).equals(event.getInventory())) {
            return;
        }
        
        int slot = event.getSlot();
        int playerSlots = dataManager.getPlayerSlots(playerUUID);
        
        // Если клик по заблокированному слоту
        if (slot >= playerSlots && slot < plugin.getMaxSlots()) {
            event.setCancelled(true);
            
            // Попытка разблокировать слот
            if (dataManager.canUnlockMoreSlots(playerUUID)) {
                if (dataManager.unlockSlot(player)) {
                    // Слот успешно разблокирован
                    String message = plugin.getMessage("slot-unlocked")
                        .replace("%cost%", String.valueOf(plugin.getCostPerSlot()));
                    player.sendMessage(message.replace("&", "§"));
                    
                    // Обновляем GUI
                    refreshGUI(player);
                } else {
                    // Недостаточно алмазов
                    String message = plugin.getMessage("not-enough-diamonds");
                    player.sendMessage(message.replace("&", "§"));
                }
            } else {
                // Достигнут максимум слотов
                String message = plugin.getMessage("max-slots-reached");
                player.sendMessage(message.replace("&", "§"));
            }
            return;
        }
        
        // Если клик по слоту вне доступной области
        if (slot >= plugin.getMaxSlots()) {
            event.setCancelled(true);
            return;
        }
        
        // Если клик по доступному слоту - разрешаем взаимодействие
        if (slot < playerSlots) {
            // Сохраняем изменения через небольшую задержку
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                saveSlotContent(playerUUID, slot, event.getInventory().getItem(slot));
            }, 1L);
        }
    }
    
    /**
     * Обработка закрытия инвентаря
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        
        // Проверяем, что это наш GUI
        if (!openGUIs.containsKey(playerUUID) || !openGUIs.get(playerUUID).equals(event.getInventory())) {
            return;
        }
        
        // Сохраняем все содержимое доступных слотов
        Inventory inventory = event.getInventory();
        int playerSlots = dataManager.getPlayerSlots(playerUUID);
        
        for (int i = 0; i < playerSlots; i++) {
            saveSlotContent(playerUUID, i, inventory.getItem(i));
        }
        
        // Убираем GUI из кэша
        openGUIs.remove(playerUUID);
    }
    
    /**
     * Сохранить содержимое слота
     */
    private void saveSlotContent(UUID playerUUID, int slot, ItemStack item) {
        dataManager.setVirtualItem(playerUUID, slot, item);
    }
    
    /**
     * Обновить GUI игрока
     */
    private void refreshGUI(Player player) {
        UUID playerUUID = player.getUniqueId();
        Inventory currentInventory = openGUIs.get(playerUUID);
        
        if (currentInventory != null) {
            // Сохраняем текущее содержимое доступных слотов
            int oldPlayerSlots = dataManager.getPlayerSlots(playerUUID) - 1; // -1 потому что уже добавили
            for (int i = 0; i < oldPlayerSlots; i++) {
                saveSlotContent(playerUUID, i, currentInventory.getItem(i));
            }
            
            // Закрываем текущий инвентарь
            player.closeInventory();
            
            // Открываем новый
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                openEnderChest(player);
            }, 1L);
        }
    }
    
    /**
     * Проверить, открыт ли GUI у игрока
     */
    public boolean hasOpenGUI(UUID playerUUID) {
        return openGUIs.containsKey(playerUUID);
    }
}