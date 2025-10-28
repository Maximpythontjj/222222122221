package com.github.jackspawner.commands;

import com.github.jackspawner.gui.JackShopGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JackShopCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                            @NotNull String label, @NotNull String[] args) {
        
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Эта команда доступна только игрокам!")
                .color(NamedTextColor.RED));
            return true;
        }
        
        // Open the shop GUI
        JackShopGUI.openShop(player);
        
        return true;
    }
}