package com.github.jackspawner.utils;

import com.github.jackspawner.JackSpawnerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ItemUtils {
    
    // NamespacedKey constants
    public static final NamespacedKey JACK_PICKAXE_KEY = new NamespacedKey(JackSpawnerPlugin.getInstance(), "is_jack_pickaxe");
    public static final NamespacedKey JACK_PICKAXE_CHANCE_KEY = new NamespacedKey(JackSpawnerPlugin.getInstance(), "jack_pickaxe_chance");
    public static final NamespacedKey MOB_TYPE_KEY = new NamespacedKey(JackSpawnerPlugin.getInstance(), "mob");
    
    // Allowed mob types
    public static final Set<EntityType> ALLOWED_MOBS = Set.of(
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.SPIDER,
        EntityType.CAVE_SPIDER,
        EntityType.WITCH,
        EntityType.GUARDIAN,
        EntityType.BLAZE,
        EntityType.SILVERFISH
    );
    
    /**
     * Creates a Jack's Pickaxe item (default golden 20%).
     */
    public static ItemStack createJackPickaxe() {
        return createJackPickaxeWith(Material.GOLDEN_PICKAXE, 20);
    }

    /**
     * Creates a Jack's Pickaxe item with specified material and chance percent.
     */
    public static ItemStack createJackPickaxeWith(Material material, int chancePercent) {
        ItemStack pickaxe = new ItemStack(material);
        ItemMeta meta = pickaxe.getItemMeta();
        
        // Set display name (same for all variants)
        meta.displayName(Component.text("Кирка Джека")
            .color(material == Material.DIAMOND_PICKAXE ? NamedTextColor.AQUA : NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));
        
        // Set lore (uses dynamic chance)
        List<Component> lore = Arrays.asList(
            Component.text("Ломает спавнер и с шансом " + chancePercent + "%")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false),
            Component.text("выпадает спавнер с мобом внутри")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        );
        meta.lore(lore);
        
        // Add visual glow (enchantment effect)
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        
        // Mark as Jack's Pickaxe in PDC
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(JACK_PICKAXE_KEY, PersistentDataType.BOOLEAN, true);
        pdc.set(JACK_PICKAXE_CHANCE_KEY, PersistentDataType.INTEGER, Math.max(0, Math.min(100, chancePercent)));
        
        pickaxe.setItemMeta(meta);
        return pickaxe;
    }

    /**
     * Convenience: creates diamond Jack's Pickaxe with 50% chance.
     */
    public static ItemStack createDiamondJackPickaxe50() {
        return createJackPickaxeWith(Material.DIAMOND_PICKAXE, 50);
    }
    
    /**
     * Checks if an item is Jack's Pickaxe
     */
    public static boolean isJackPickaxe(ItemStack item) {
        if (item == null) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(JACK_PICKAXE_KEY, PersistentDataType.BOOLEAN) &&
               Boolean.TRUE.equals(pdc.get(JACK_PICKAXE_KEY, PersistentDataType.BOOLEAN));
    }

    /**
     * Reads the stored chance percent from a Jack's Pickaxe. Defaults to 20 if absent.
     */
    public static int getJackPickaxeChancePercent(ItemStack item) {
        if (item == null) {
            return 20;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 20;
        }
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Integer value = pdc.get(JACK_PICKAXE_CHANCE_KEY, PersistentDataType.INTEGER);
        if (value == null) {
            return 20;
        }
        return Math.max(0, Math.min(100, value));
    }
    
    /**
     * Creates a custom spawner item for the specified mob type
     */
    public static ItemStack createCustomSpawner(EntityType mobType) {
        if (!ALLOWED_MOBS.contains(mobType)) {
            return null;
        }
        
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        ItemMeta meta = spawner.getItemMeta();
        
        // Set display name based on mob type
        String displayName = getMobDisplayName(mobType);
        meta.displayName(Component.text(displayName)
            .color(NamedTextColor.YELLOW)
            .decoration(TextDecoration.ITALIC, false));
        
        // Store mob type in PDC
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(MOB_TYPE_KEY, PersistentDataType.STRING, mobType.name());
        
        spawner.setItemMeta(meta);
        return spawner;
    }
    
    /**
     * Gets the mob type from a custom spawner item
     */
    public static EntityType getMobTypeFromSpawner(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(MOB_TYPE_KEY, PersistentDataType.STRING)) {
            return null;
        }
        
        String mobTypeName = pdc.get(MOB_TYPE_KEY, PersistentDataType.STRING);
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
     * Gets the Russian display name for a mob type
     */
    private static String getMobDisplayName(EntityType mobType) {
        return switch (mobType) {
            case ZOMBIE -> "Спавнер зомби";
            case SKELETON -> "Спавнер скелетов";
            case SPIDER -> "Спавнер пауков";
            case CAVE_SPIDER -> "Спавнер пещерных пауков";
            case WITCH -> "Спавнер ведьм";
            case GUARDIAN -> "Спавнер стражей";
            case BLAZE -> "Спавнер ифритов";
            case SILVERFISH -> "Спавнер чешуйниц";
            default -> "Спавнер";
        };
    }
}