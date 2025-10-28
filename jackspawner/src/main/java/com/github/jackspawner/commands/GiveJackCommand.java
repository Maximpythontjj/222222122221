package com.github.jackspawner.commands;

import com.github.jackspawner.utils.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiveJackCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!")
                .color(NamedTextColor.RED));
            return true;
        }
        
        if (!player.hasPermission("jackspawner.give")) {
            player.sendMessage(Component.text("У вас нет прав для использования этой команды!")
                .color(NamedTextColor.RED));
            return true;
        }
        
        // Determine which type of pickaxe to give
        int dropChance = 20; // Default is golden pickaxe with 20% chance
        
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("diamond") || args[0].equals("50")) {
                dropChance = 50;
            } else if (args[0].equalsIgnoreCase("gold") || args[0].equals("20")) {
                dropChance = 20;
            }
        }
        
        // Create and give Jack's Pickaxe
        ItemStack jackPickaxe = ItemUtils.createJackPickaxe(dropChance);
        player.getInventory().addItem(jackPickaxe);
        
        String pickaxeType = dropChance == 50 ? "Алмазную Кирку Джека (50%)" : "Золотую Кирку Джека (20%)";
        player.sendMessage(Component.text("Вы получили " + pickaxeType + "!")
            .color(NamedTextColor.GREEN));
        
        return true;
    }
}