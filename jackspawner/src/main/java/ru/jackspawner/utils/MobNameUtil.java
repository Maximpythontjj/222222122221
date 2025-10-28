package ru.jackspawner.utils;

import org.bukkit.entity.EntityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class MobNameUtil {
    
    private static final Map<EntityType, String> MOB_NAMES = new HashMap<>();
    
    static {
        MOB_NAMES.put(EntityType.ZOMBIE, "Спавнер зомби");
        MOB_NAMES.put(EntityType.SKELETON, "Спавнер скелетов");
        MOB_NAMES.put(EntityType.SPIDER, "Спавнер пауков");
        MOB_NAMES.put(EntityType.CAVE_SPIDER, "Спавнер пещерных пауков");
        MOB_NAMES.put(EntityType.WITCH, "Спавнер ведьм");
        MOB_NAMES.put(EntityType.GUARDIAN, "Спавнер стражей");
        MOB_NAMES.put(EntityType.BLAZE, "Спавнер ифритов");
        MOB_NAMES.put(EntityType.SILVERFISH, "Спавнер чешуйниц");
    }
    
    /**
     * Получить русское название спавнера для указанного типа моба
     * @param entityType тип моба
     * @return русское название или null, если тип не поддерживается
     */
    public static String getSpawnerName(EntityType entityType) {
        return MOB_NAMES.get(entityType);
    }
    
    /**
     * Проверить, поддерживается ли данный тип моба
     * @param entityType тип моба
     * @return true, если поддерживается
     */
    public static boolean isAllowedMob(EntityType entityType) {
        return MOB_NAMES.containsKey(entityType);
    }
    
    /**
     * Получить все поддерживаемые типы мобов
     * @return множество поддерживаемых типов
     */
    public static Set<EntityType> getAllowedMobs() {
        return MOB_NAMES.keySet();
    }
    
    private MobNameUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
}