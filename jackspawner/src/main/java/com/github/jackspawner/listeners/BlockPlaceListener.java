package com.github.jackspawner.listeners;

import com.github.jackspawner.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlaceListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack itemPlaced = event.getItemInHand();
        
        // Check if it's a spawner
        if (itemPlaced.getType() != Material.SPAWNER) {
            return;
        }
        
        // Check if it's our custom spawner
        EntityType mobType = ItemUtils.getMobTypeFromSpawner(itemPlaced);
        if (mobType == null) {
            return;
        }
        
        // Set the spawner type
        Block block = event.getBlockPlaced();
        if (block.getState() instanceof CreatureSpawner spawner) {
            spawner.setSpawnedType(mobType);
            spawner.update(true, false);
        }
    }
}