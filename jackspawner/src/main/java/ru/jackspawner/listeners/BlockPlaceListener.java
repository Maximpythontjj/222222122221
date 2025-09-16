package ru.jackspawner.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import ru.jackspawner.utils.ItemUtil;
import ru.jackspawner.utils.MobNameUtil;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItemInHand();
        Block placedBlock = event.getBlockPlaced();
        
        // Проверяем, что ставим спавнер
        if (placedBlock.getType() != Material.SPAWNER) {
            return;
        }
        
        // Проверяем, является ли предмет кастомным спавнером
        EntityType mobType = ItemUtil.getMobTypeFromSpawner(itemInHand);
        if (mobType == null || !MobNameUtil.isAllowedMob(mobType)) {
            return;
        }
        
        // Получаем блок спавнера и устанавливаем тип моба
        CreatureSpawner spawner = (CreatureSpawner) placedBlock.getState();
        spawner.setSpawnedType(mobType);
        spawner.update(true, false);
    }
}