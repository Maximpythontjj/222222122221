package ru.jackspawner.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.jackspawner.utils.ItemUtil;

public class GiveJackCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверяем, что команду выполняет игрок
        if (!(sender instanceof Player)) {
            sender.sendMessage("Эта команда доступна только игрокам!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Проверяем права доступа
        if (!player.hasPermission("jackspawner.give")) {
            player.sendMessage("У вас нет прав на использование этой команды!");
            return true;
        }
        
        // Создаём и выдаём Кирку Джека
        ItemStack jackPickaxe = ItemUtil.createJackPickaxe();
        player.getInventory().addItem(jackPickaxe);
        player.sendMessage("Вы получили Кирку Джека!");
        
        return true;
    }
}