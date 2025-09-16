package com.github.jackspawner.commands;

import com.github.jackspawner.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class JackShopCommand implements CommandExecutor, Listener {
    
    private static final String SHOP_TITLE = "Магазин Кирки Джека";
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!")
                .color(NamedTextColor.RED));
            return true;
        }
        
        openShop(player);
        return true;
    }
    
    private void openShop(Player player) {
        Inventory shopInventory = Bukkit.createInventory(null, 27, Component.text(SHOP_TITLE));
        
        // Golden Jack's Pickaxe (20% chance) - 15 diamonds
        ItemStack goldenPickaxe = ItemUtils.createGoldenJackPickaxe();
        ItemMeta goldenMeta = goldenPickaxe.getItemMeta();
        List<Component> goldenLore = goldenMeta.lore();
        goldenLore.add(Component.text(""));
        goldenLore.add(Component.text("Цена: 15 алмазов")
            .color(NamedTextColor.GREEN));
        goldenMeta.lore(goldenLore);
        goldenPickaxe.setItemMeta(goldenMeta);
        
        // Diamond Jack's Pickaxe (50% chance) - 45 diamonds
        ItemStack diamondPickaxe = ItemUtils.createDiamondJackPickaxe();
        ItemMeta diamondMeta = diamondPickaxe.getItemMeta();
        List<Component> diamondLore = diamondMeta.lore();
        diamondLore.add(Component.text(""));
        diamondLore.add(Component.text("Цена: 45 алмазов")
            .color(NamedTextColor.GREEN));
        diamondMeta.lore(diamondLore);
        diamondPickaxe.setItemMeta(diamondMeta);
        
        // Create decorative items
        ItemStack glassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.displayName(Component.text(" "));
        glassPane.setItemMeta(glassMeta);
        
        // Fill inventory with decorative items
        for (int i = 0; i < 27; i++) {
            shopInventory.setItem(i, glassPane);
        }
        
        // Place pickaxes in highlighted positions
        shopInventory.setItem(11, goldenPickaxe);  // Golden pickaxe
        shopInventory.setItem(15, diamondPickaxe); // Diamond pickaxe
        
        // Create highlighted frames around pickaxes
        ItemStack yellowGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta yellowMeta = yellowGlass.getItemMeta();
        yellowMeta.displayName(Component.text("Золотая Кирка Джека")
            .color(NamedTextColor.GOLD));
        yellowGlass.setItemMeta(yellowMeta);
        
        ItemStack cyanGlass = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta cyanMeta = cyanGlass.getItemMeta();
        cyanMeta.displayName(Component.text("Алмазная Кирка Джека")
            .color(NamedTextColor.AQUA));
        cyanGlass.setItemMeta(cyanMeta);
        
        // Frame for golden pickaxe
        shopInventory.setItem(2, yellowGlass);
        shopInventory.setItem(3, yellowGlass);
        shopInventory.setItem(4, yellowGlass);
        shopInventory.setItem(10, yellowGlass);
        shopInventory.setItem(12, yellowGlass);
        shopInventory.setItem(19, yellowGlass);
        shopInventory.setItem(20, yellowGlass);
        shopInventory.setItem(21, yellowGlass);
        
        // Frame for diamond pickaxe
        shopInventory.setItem(5, cyanGlass);
        shopInventory.setItem(6, cyanGlass);
        shopInventory.setItem(7, cyanGlass);
        shopInventory.setItem(14, cyanGlass);
        shopInventory.setItem(16, cyanGlass);
        shopInventory.setItem(23, cyanGlass);
        shopInventory.setItem(24, cyanGlass);
        shopInventory.setItem(25, cyanGlass);
        
        player.openInventory(shopInventory);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!event.getView().title().equals(Component.text(SHOP_TITLE))) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }
        
        int slot = event.getSlot();
        
        // Check if clicked on golden pickaxe (slot 11)
        if (slot == 11 && ItemUtils.isJackPickaxe(clickedItem)) {
            String pickaxeType = ItemUtils.getPickaxeType(clickedItem);
            if (ItemUtils.GOLDEN_JACK_PICKAXE.equals(pickaxeType)) {
                handlePurchase(player, ItemUtils.createGoldenJackPickaxe(), 15);
            }
        }
        // Check if clicked on diamond pickaxe (slot 15)
        else if (slot == 15 && ItemUtils.isJackPickaxe(clickedItem)) {
            String pickaxeType = ItemUtils.getPickaxeType(clickedItem);
            if (ItemUtils.DIAMOND_JACK_PICKAXE.equals(pickaxeType)) {
                handlePurchase(player, ItemUtils.createDiamondJackPickaxe(), 45);
            }
        }
    }
    
    private void handlePurchase(Player player, ItemStack item, int price) {
        // Check if player has enough diamonds
        int diamondCount = 0;
        for (ItemStack invItem : player.getInventory().getContents()) {
            if (invItem != null && invItem.getType() == Material.DIAMOND) {
                diamondCount += invItem.getAmount();
            }
        }
        
        if (diamondCount < price) {
            player.sendMessage(Component.text("У вас недостаточно алмазов! Нужно: " + price + ", у вас: " + diamondCount)
                .color(NamedTextColor.RED));
            return;
        }
        
        // Remove diamonds from inventory
        int remaining = price;
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack invItem = player.getInventory().getItem(i);
            if (invItem != null && invItem.getType() == Material.DIAMOND) {
                int amount = invItem.getAmount();
                if (amount <= remaining) {
                    remaining -= amount;
                    player.getInventory().setItem(i, null);
                } else {
                    invItem.setAmount(amount - remaining);
                    remaining = 0;
                }
            }
        }
        
        // Give item to player
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
        
        String itemName = ItemUtils.GOLDEN_JACK_PICKAXE.equals(ItemUtils.getPickaxeType(item)) ? 
            "Золотую Кирку Джека" : "Алмазную Кирку Джека";
        
        player.sendMessage(Component.text("Вы купили " + itemName + " за " + price + " алмазов!")
            .color(NamedTextColor.GREEN));
        
        player.closeInventory();
    }
}