package com.github.jackspawner.gui;

import com.github.jackspawner.JackSpawnerPlugin;
import com.github.jackspawner.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class JackShopGUI implements InventoryHolder, Listener {
    
    private static final int GOLDEN_PICKAXE_SLOT = 11;
    private static final int DIAMOND_PICKAXE_SLOT = 15;
    private static final int GOLDEN_PICKAXE_PRICE = 15; // 15 diamonds
    private static final int DIAMOND_PICKAXE_PRICE = 45; // 45 diamonds
    
    private final Inventory inventory;
    
    public JackShopGUI() {
        this.inventory = Bukkit.createInventory(this, 27, 
            Component.text("Магазин Кирок Джека")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true));
        
        initializeItems();
    }
    
    private void initializeItems() {
        // Fill with glass panes
        ItemStack glassPane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glassPane.getItemMeta();
        glassMeta.displayName(Component.text(" "));
        glassPane.setItemMeta(glassMeta);
        
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, glassPane);
        }
        
        // Golden Jack's Pickaxe (20% chance)
        ItemStack goldenPickaxe = ItemUtils.createJackPickaxe(20);
        ItemMeta goldenMeta = goldenPickaxe.getItemMeta();
        List<Component> goldenLore = goldenMeta.lore();
        goldenLore.add(Component.text(""));
        goldenLore.add(Component.text("Цена: " + GOLDEN_PICKAXE_PRICE + " алмазов")
            .color(NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false));
        goldenLore.add(Component.text("Нажмите для покупки")
            .color(NamedTextColor.YELLOW)
            .decoration(TextDecoration.ITALIC, false));
        goldenMeta.lore(goldenLore);
        goldenPickaxe.setItemMeta(goldenMeta);
        
        // Diamond Jack's Pickaxe (50% chance)
        ItemStack diamondPickaxe = ItemUtils.createJackPickaxe(50);
        ItemMeta diamondMeta = diamondPickaxe.getItemMeta();
        List<Component> diamondLore = diamondMeta.lore();
        diamondLore.add(Component.text(""));
        diamondLore.add(Component.text("Цена: " + DIAMOND_PICKAXE_PRICE + " алмазов")
            .color(NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false));
        diamondLore.add(Component.text("Нажмите для покупки")
            .color(NamedTextColor.YELLOW)
            .decoration(TextDecoration.ITALIC, false));
        diamondMeta.lore(diamondLore);
        diamondPickaxe.setItemMeta(diamondMeta);
        
        // Place items
        inventory.setItem(GOLDEN_PICKAXE_SLOT, goldenPickaxe);
        inventory.setItem(DIAMOND_PICKAXE_SLOT, diamondPickaxe);
        
        // Add decorative items
        ItemStack goldBlock = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta goldBlockMeta = goldBlock.getItemMeta();
        goldBlockMeta.displayName(Component.text("Золотая кирка")
            .color(NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false));
        goldBlockMeta.lore(Arrays.asList(
            Component.text("20% шанс выпадения")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));
        goldBlock.setItemMeta(goldBlockMeta);
        
        ItemStack diamondBlock = new ItemStack(Material.DIAMOND_BLOCK);
        ItemMeta diamondBlockMeta = diamondBlock.getItemMeta();
        diamondBlockMeta.displayName(Component.text("Алмазная кирка")
            .color(NamedTextColor.AQUA)
            .decoration(TextDecoration.ITALIC, false));
        diamondBlockMeta.lore(Arrays.asList(
            Component.text("50% шанс выпадения")
                .color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));
        diamondBlock.setItemMeta(diamondBlockMeta);
        
        inventory.setItem(GOLDEN_PICKAXE_SLOT - 9, goldBlock);
        inventory.setItem(DIAMOND_PICKAXE_SLOT - 9, diamondBlock);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof JackShopGUI)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getClickedInventory() == null || event.getClickedInventory().getHolder() != this) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        if (slot == GOLDEN_PICKAXE_SLOT) {
            purchasePickaxe(player, 20, GOLDEN_PICKAXE_PRICE);
        } else if (slot == DIAMOND_PICKAXE_SLOT) {
            purchasePickaxe(player, 50, DIAMOND_PICKAXE_PRICE);
        }
    }
    
    private void purchasePickaxe(Player player, int dropChance, int price) {
        // Check if player has enough diamonds
        int diamondCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND) {
                diamondCount += item.getAmount();
            }
        }
        
        if (diamondCount < price) {
            player.sendMessage(Component.text("У вас недостаточно алмазов! Нужно " + price + " алмазов.")
                .color(NamedTextColor.RED));
            player.closeInventory();
            return;
        }
        
        // Remove diamonds from inventory
        int diamondsToRemove = price;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND) {
                int amount = item.getAmount();
                if (amount <= diamondsToRemove) {
                    player.getInventory().remove(item);
                    diamondsToRemove -= amount;
                } else {
                    item.setAmount(amount - diamondsToRemove);
                    diamondsToRemove = 0;
                }
                
                if (diamondsToRemove == 0) {
                    break;
                }
            }
        }
        
        // Give the pickaxe
        ItemStack pickaxe = ItemUtils.createJackPickaxe(dropChance);
        player.getInventory().addItem(pickaxe);
        
        String pickaxeType = dropChance == 50 ? "Алмазную Кирку Джека (50%)" : "Золотую Кирку Джека (20%)";
        player.sendMessage(Component.text("Вы купили " + pickaxeType + " за " + price + " алмазов!")
            .color(NamedTextColor.GREEN));
        
        player.closeInventory();
    }
    
    public static void openShop(Player player) {
        JackShopGUI gui = new JackShopGUI();
        player.openInventory(gui.getInventory());
    }
    
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}