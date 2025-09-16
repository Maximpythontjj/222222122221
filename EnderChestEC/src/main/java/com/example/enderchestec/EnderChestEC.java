package com.example.enderchestec;

import com.example.enderchestec.commands.EnderChestCommand;
import com.example.enderchestec.storage.PlayerDataManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Главный класс плагина EnderChestEC
 * Расширяемый эндер-сундук с командой /ec
 */
public final class EnderChestEC extends JavaPlugin {
    
    private static EnderChestEC instance;
    private PlayerDataManager playerDataManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Создаем папку плагина если её нет
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Копируем конфиги по умолчанию
        saveDefaultConfigs();
        
        // Инициализируем менеджер данных игроков
        playerDataManager = new PlayerDataManager(this);
        
        // Регистрируем команду
        getCommand("ec").setExecutor(new EnderChestCommand(this));
        
        getLogger().info("EnderChestEC успешно загружен!");
    }
    
    @Override
    public void onDisable() {
        // Сохраняем данные игроков при отключении
        if (playerDataManager != null) {
            playerDataManager.savePlayerData();
        }
        
        getLogger().info("EnderChestEC отключен!");
    }
    
    /**
     * Получить инстанс плагина
     */
    public static EnderChestEC getInstance() {
        return instance;
    }
    
    /**
     * Получить менеджер данных игроков
     */
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    /**
     * Сохранить конфиги по умолчанию
     */
    private void saveDefaultConfigs() {
        // Сохраняем config.yml
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        
        // Сохраняем player-settings.yml
        File playerSettingsFile = new File(getDataFolder(), "player-settings.yml");
        if (!playerSettingsFile.exists()) {
            try {
                InputStream inputStream = getResource("player-settings.yml");
                if (inputStream != null) {
                    Files.copy(inputStream, playerSettingsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    inputStream.close();
                }
            } catch (IOException e) {
                getLogger().severe("Не удалось создать player-settings.yml: " + e.getMessage());
            }
        }
        
        // Перезагружаем конфиг
        reloadConfig();
    }
    
    /**
     * Получить количество слотов по умолчанию
     */
    public int getDefaultSlots() {
        return getConfig().getInt("default-slots", 27);
    }
    
    /**
     * Получить максимальное количество слотов
     */
    public int getMaxSlots() {
        return getConfig().getInt("max-slots", 81);
    }
    
    /**
     * Получить стоимость за слот
     */
    public int getCostPerSlot() {
        return getConfig().getInt("cost-per-slot", 5);
    }
    
    /**
     * Получить сообщение из конфига
     */
    public String getMessage(String key) {
        return getConfig().getString("messages." + key, "Сообщение не найдено: " + key);
    }
}