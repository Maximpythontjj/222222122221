package ru.jackspawner.utils;

import org.bukkit.NamespacedKey;
import ru.jackspawner.JackSpawnerPlugin;

public final class Constants {
    
    // Ключи для PersistentDataContainer
    public static final NamespacedKey JACK_PICKAXE_KEY = new NamespacedKey(JackSpawnerPlugin.getInstance(), "is_jack_pickaxe");
    public static final NamespacedKey MOB_TYPE_KEY = new NamespacedKey(JackSpawnerPlugin.getInstance(), "mob_type");
    
    // Шанс дропа спавнера
    public static final double DROP_CHANCE = 0.2;
    
    // Названия предметов
    public static final String JACK_PICKAXE_NAME = "Кирка Джека";
    
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }
}