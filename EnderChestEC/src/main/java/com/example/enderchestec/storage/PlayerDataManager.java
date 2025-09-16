package com.example.enderchestec.storage;

import com.example.enderchestec.EnderChestEC;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер для работы с данными игроков
 * Управляет количеством доступных слотов для каждого игрока
 */
public class PlayerDataManager {
    
    private final EnderChestEC plugin;
    private final File playerDataFile;
    private FileConfiguration playerData;
    
    // Кэш данных игроков для быстрого доступа
    private final Map<UUID, Integer> playerSlots = new ConcurrentHashMap<>();
    
    // Виртуальные инвентари игроков
    private final Map<UUID, Map<Integer, org.bukkit.inventory.ItemStack>> virtualInventories = new ConcurrentHashMap<>();
    
    public PlayerDataManager(EnderChestEC plugin) {
        this.plugin = plugin;
        this.playerDataFile = new File(plugin.getDataFolder(), "player-settings.yml");
        
        loadPlayerData();
    }
    
    /**
     * Загрузить данные игроков из файла
     */
    public void loadPlayerData() {
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать player-settings.yml: " + e.getMessage());
                return;
            }
        }
        
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
        
        // Загружаем данные в кэш
        if (playerData.contains("players")) {
            for (String uuidString : playerData.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    int slots = playerData.getInt("players." + uuidString + ".slots", plugin.getDefaultSlots());
                    playerSlots.put(uuid, slots);
                    
                    // Загружаем виртуальный инвентарь если есть
                    if (playerData.contains("players." + uuidString + ".inventory")) {
                        Map<Integer, org.bukkit.inventory.ItemStack> inventory = new HashMap<>();
                        for (String slotString : playerData.getConfigurationSection("players." + uuidString + ".inventory").getKeys(false)) {
                            try {
                                int slot = Integer.parseInt(slotString);
                                org.bukkit.inventory.ItemStack item = playerData.getItemStack("players." + uuidString + ".inventory." + slotString);
                                if (item != null) {
                                    inventory.put(slot, item);
                                }
                            } catch (NumberFormatException ignored) {}
                        }
                        virtualInventories.put(uuid, inventory);
                    }
                } catch (IllegalArgumentException ignored) {
                    // Некорректный UUID, пропускаем
                }
            }
        }
        
        plugin.getLogger().info("Загружено данных игроков: " + playerSlots.size());
    }
    
    /**
     * Сохранить данные игроков в файл
     */
    public void savePlayerData() {
        try {
            // Сохраняем количество слотов
            for (Map.Entry<UUID, Integer> entry : playerSlots.entrySet()) {
                String uuidString = entry.getKey().toString();
                playerData.set("players." + uuidString + ".slots", entry.getValue());
            }
            
            // Сохраняем виртуальные инвентари
            for (Map.Entry<UUID, Map<Integer, org.bukkit.inventory.ItemStack>> entry : virtualInventories.entrySet()) {
                String uuidString = entry.getKey().toString();
                
                // Очищаем старые данные инвентаря
                playerData.set("players." + uuidString + ".inventory", null);
                
                // Сохраняем новые данные
                for (Map.Entry<Integer, org.bukkit.inventory.ItemStack> invEntry : entry.getValue().entrySet()) {
                    if (invEntry.getValue() != null) {
                        playerData.set("players." + uuidString + ".inventory." + invEntry.getKey(), invEntry.getValue());
                    }
                }
            }
            
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить player-settings.yml: " + e.getMessage());
        }
    }
    
    /**
     * Получить количество слотов игрока
     */
    public int getPlayerSlots(UUID playerUUID) {
        return playerSlots.getOrDefault(playerUUID, plugin.getDefaultSlots());
    }
    
    /**
     * Установить количество слотов игрока
     */
    public void setPlayerSlots(UUID playerUUID, int slots) {
        // Проверяем лимиты
        int maxSlots = plugin.getMaxSlots();
        int defaultSlots = plugin.getDefaultSlots();
        
        slots = Math.max(defaultSlots, Math.min(maxSlots, slots));
        
        playerSlots.put(playerUUID, slots);
        
        // Асинхронно сохраняем данные
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
    }
    
    /**
     * Добавить слоты игроку
     */
    public boolean addPlayerSlots(UUID playerUUID, int slotsToAdd) {
        int currentSlots = getPlayerSlots(playerUUID);
        int newSlots = currentSlots + slotsToAdd;
        
        if (newSlots > plugin.getMaxSlots()) {
            return false; // Превышен лимит
        }
        
        setPlayerSlots(playerUUID, newSlots);
        return true;
    }
    
    /**
     * Проверить, может ли игрок разблокировать еще слоты
     */
    public boolean canUnlockMoreSlots(UUID playerUUID) {
        return getPlayerSlots(playerUUID) < plugin.getMaxSlots();
    }
    
    /**
     * Разблокировать слот для игрока
     */
    public boolean unlockSlot(Player player) {
        if (!canUnlockMoreSlots(player.getUniqueId())) {
            return false;
        }
        
        // Проверяем наличие алмазов
        int cost = plugin.getCostPerSlot();
        if (!hasEnoughDiamonds(player, cost)) {
            return false;
        }
        
        // Списываем алмазы
        removeDiamonds(player, cost);
        
        // Добавляем слот
        return addPlayerSlots(player.getUniqueId(), 1);
    }
    
    /**
     * Проверить, есть ли у игрока достаточно алмазов
     */
    private boolean hasEnoughDiamonds(Player player, int amount) {
        return player.getInventory().containsAtLeast(new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND), amount);
    }
    
    /**
     * Убрать алмазы из инвентаря игрока
     */
    private void removeDiamonds(Player player, int amount) {
        org.bukkit.inventory.ItemStack diamonds = new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND, amount);
        player.getInventory().removeItem(diamonds);
    }
    
    /**
     * Получить виртуальный инвентарь игрока
     */
    public Map<Integer, org.bukkit.inventory.ItemStack> getVirtualInventory(UUID playerUUID) {
        return virtualInventories.computeIfAbsent(playerUUID, k -> new HashMap<>());
    }
    
    /**
     * Сохранить предмет в виртуальный инвентарь
     */
    public void setVirtualItem(UUID playerUUID, int slot, org.bukkit.inventory.ItemStack item) {
        Map<Integer, org.bukkit.inventory.ItemStack> inventory = getVirtualInventory(playerUUID);
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            inventory.remove(slot);
        } else {
            inventory.put(slot, item.clone());
        }
        
        // Асинхронно сохраняем данные
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
    }
    
    /**
     * Получить предмет из виртуального инвентаря
     */
    public org.bukkit.inventory.ItemStack getVirtualItem(UUID playerUUID, int slot) {
        Map<Integer, org.bukkit.inventory.ItemStack> inventory = getVirtualInventory(playerUUID);
        org.bukkit.inventory.ItemStack item = inventory.get(slot);
        return item != null ? item.clone() : null;
    }
    
    /**
     * Инициализировать игрока при первом входе
     */
    public void initializePlayer(UUID playerUUID) {
        if (!playerSlots.containsKey(playerUUID)) {
            playerSlots.put(playerUUID, plugin.getDefaultSlots());
            virtualInventories.put(playerUUID, new HashMap<>());
            
            // Асинхронно сохраняем данные
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::savePlayerData);
        }
    }
}