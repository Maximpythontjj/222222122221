package ru.jackspawner.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import ru.jackspawner.utils.ItemUtil;
import ru.jackspawner.utils.MobNameUtil;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        // Проверяем, использует ли игрок Кирку Джека
        if (!ItemUtil.isJackPickaxe(itemInHand)) {
            return;
        }
        
        Block block = event.getBlock();
        
        // Если ломаем не спавнер - отменяем событие
        if (block.getType() != Material.SPAWNER) {
            event.setCancelled(true);
            return;
        }
        
        // Получаем данные спавнера
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType spawnedType = spawner.getSpawnedType();
        
        // Отменяем стандартный дроп
        event.setDropItems(false);
        
        // Удаляем блок
        block.setType(Material.AIR);
        
        // Проверяем, разрешён ли тип моба
        if (spawnedType != null && MobNameUtil.isAllowedMob(spawnedType)) {
            // С шансом 20% дропаем кастомный спавнер
            if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                ItemStack customSpawner = ItemUtil.createCustomSpawner(spawnedType);
                if (customSpawner != null) {
                    block.getWorld().dropItemNaturally(block.getLocation(), customSpawner);
                }
            }
        }
        
        // Расходуем кирку (одноразовая)
        player.getInventory().setItemInMainHand(null);
    }
}