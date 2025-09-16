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
        
        ItemStack jackPickaxe = ItemUtils.createJackPickaxe();
        player.getInventory().addItem(jackPickaxe);
        
        player.sendMessage(Component.text("Вы получили Кирку Джека!")
            .color(NamedTextColor.GREEN));
        
        return true;
    }
}