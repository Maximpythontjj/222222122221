package com.github.jackspawner.listeners;

import com.github.jackspawner.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBreakListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        
        // Check if player is using Jack's Pickaxe
        if (!ItemUtils.isJackPickaxe(itemInHand)) {
            return;
        }
        
        Block block = event.getBlock();
        
        // If not a spawner, cancel the break
        if (block.getType() != Material.SPAWNER) {
            event.setCancelled(true);
            return;
        }
        
        // Handle spawner breaking
        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        EntityType spawnedType = spawner.getSpawnedType();
        
        // Cancel default drops
        event.setDropItems(false);
        
        // Check if this is an allowed mob type
        if (ItemUtils.ALLOWED_MOBS.contains(spawnedType)) {
            // Get pickaxe type and corresponding drop chance
            String pickaxeType = ItemUtils.getPickaxeType(itemInHand);
            double dropChance = ItemUtils.getDropChance(pickaxeType);
            
            // Check if spawner should drop based on pickaxe type
            if (ThreadLocalRandom.current().nextDouble() < dropChance) {
                ItemStack customSpawner = ItemUtils.createCustomSpawner(spawnedType);
                if (customSpawner != null) {
                    Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
                    block.getWorld().dropItemNaturally(dropLocation, customSpawner);
                }
            }
        }
        
        // Remove the block
        block.setType(Material.AIR);
        
        // Remove Jack's Pickaxe (one-time use)
        player.getInventory().setItemInMainHand(null);
    }
}