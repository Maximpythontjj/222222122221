package com.github.jackspawner.commands;

import com.github.jackspawner.ui.JackShopHolder;
import com.github.jackspawner.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class JackShopCommand implements CommandExecutor {

    public static final String SHOP_TITLE = "Магазин кирки Джека";
    public static final int SLOT_GOLD = 11;
    public static final int SLOT_DIAMOND = 15;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!").color(NamedTextColor.RED));
            return true;
        }

        openShop(player);
        return true;
    }

    public static void openShop(Player player) {
        JackShopHolder holder = new JackShopHolder();
        Inventory inv = Bukkit.createInventory(holder, 27, Component.text(SHOP_TITLE)
            .decoration(TextDecoration.ITALIC, false));
        holder.setInventory(inv);

        // Background like chest UI
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(Component.text(" "));
        fillerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Gold 20% option, price 15 diamonds
        ItemStack gold = ItemUtils.createJackPickaxeWith(Material.GOLDEN_PICKAXE, 20);
        ItemMeta goldMeta = gold.getItemMeta();
        goldMeta.lore(java.util.List.of(
            Component.text("Цена: 15 алмазов").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)
        ));
        gold.setItemMeta(goldMeta);
        inv.setItem(SLOT_GOLD, gold);

        // Diamond 50% option, price 45 diamonds
        ItemStack diamond = ItemUtils.createJackPickaxeWith(Material.DIAMOND_PICKAXE, 50);
        ItemMeta diamondMeta = diamond.getItemMeta();
        diamondMeta.lore(java.util.List.of(
            Component.text("Цена: 45 алмазов").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
        ));
        diamond.setItemMeta(diamondMeta);
        inv.setItem(SLOT_DIAMOND, diamond);

        player.openInventory(inv);
    }
}

