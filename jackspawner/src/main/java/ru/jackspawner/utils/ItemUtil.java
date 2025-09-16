package ru.jackspawner.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public final class ItemUtil {
    
    /**
     * Создать Кирку Джека
     * @return ItemStack с киркой Джека
     */
    public static ItemStack createJackPickaxe() {
        ItemStack pickaxe = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta meta = pickaxe.getItemMeta();
        
        if (meta != null) {
            // Устанавливаем название
            meta.setDisplayName(Constants.JACK_PICKAXE_NAME);
            
            // Добавляем лор
            meta.setLore(Arrays.asList(
                "Ломает спавнер и с шансом 20%",
                "выпадает спавнер с мобом внутри"
            ));
            
            // Добавляем свечение через бесполезный энчант
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            
            // Помечаем в PDC как Кирку Джека
            meta.getPersistentDataContainer().set(Constants.JACK_PICKAXE_KEY, PersistentDataType.BOOLEAN, true);
            
            pickaxe.setItemMeta(meta);
        }
        
        return pickaxe;
    }
    
    /**
     * Проверить, является ли предмет Киркой Джека
     * @param item предмет для проверки
     * @return true, если это Кирка Джека
     */
    public static boolean isJackPickaxe(ItemStack item) {
        if (item == null || item.getType() != Material.GOLDEN_PICKAXE) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        return meta.getPersistentDataContainer().has(Constants.JACK_PICKAXE_KEY, PersistentDataType.BOOLEAN);
    }
    
    /**
     * Создать кастомный спавнер для указанного типа моба
     * @param entityType тип моба
     * @return ItemStack со спавнером или null, если тип не поддерживается
     */
    public static ItemStack createCustomSpawner(EntityType entityType) {
        String spawnerName = MobNameUtil.getSpawnerName(entityType);
        if (spawnerName == null) {
            return null;
        }
        
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        
        if (meta != null) {
            // Устанавливаем название
            meta.setDisplayName(spawnerName);
            
            // Сохраняем тип моба в PDC
            meta.getPersistentDataContainer().set(Constants.MOB_TYPE_KEY, PersistentDataType.STRING, entityType.name());
            
            spawner.setItemMeta(meta);
        }
        
        return spawner;
    }
    
    /**
     * Получить тип моба из кастомного спавнера
     * @param item предмет-спавнер
     * @return EntityType или null, если не найден
     */
    public static EntityType getMobTypeFromSpawner(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        String mobTypeName = meta.getPersistentDataContainer().get(Constants.MOB_TYPE_KEY, PersistentDataType.STRING);
        if (mobTypeName == null) {
            return null;
        }
        
        try {
            return EntityType.valueOf(mobTypeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Проверить, является ли предмет кастомным спавнером
     * @param item предмет для проверки
     * @return true, если это кастомный спавнер
     */
    public static boolean isCustomSpawner(ItemStack item) {
        return getMobTypeFromSpawner(item) != null;
    }
    
    private ItemUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
}