package com.example.enderchestec.commands;

import com.example.enderchestec.EnderChestEC;
import com.example.enderchestec.gui.EnderChestGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Команда /ec для открытия расширяемого эндер-сундука
 */
public class EnderChestCommand implements CommandExecutor {
    
    private final EnderChestEC plugin;
    private final EnderChestGUI gui;
    
    public EnderChestCommand(EnderChestEC plugin) {
        this.plugin = plugin;
        this.gui = new EnderChestGUI(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Проверяем, что команду выполняет игрок
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда доступна только игрокам!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Проверяем права
        if (!player.hasPermission("enderchestec.use")) {
            player.sendMessage("§cУ вас нет прав для использования этой команды!");
            return true;
        }
        
        // Обрабатываем подкоманды
        if (args.length > 0) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "help":
                case "?":
                    sendHelp(player);
                    return true;
                    
                case "info":
                    sendInfo(player);
                    return true;
                    
                case "reload":
                    if (player.hasPermission("enderchestec.admin")) {
                        reloadPlugin(player);
                    } else {
                        player.sendMessage("§cУ вас нет прав для перезагрузки плагина!");
                    }
                    return true;
                    
                default:
                    player.sendMessage("§cНеизвестная подкоманда. Используйте /ec help для справки.");
                    return true;
            }
        }
        
        // Основная функция - открыть эндер-сундук
        gui.openEnderChest(player);
        return true;
    }
    
    /**
     * Отправить справку игроку
     */
    private void sendHelp(Player player) {
        player.sendMessage("§6§l=== EnderChestEC Справка ===");
        player.sendMessage("§e/ec §7- Открыть расширяемый эндер-сундук");
        player.sendMessage("§e/ec info §7- Информация о ваших слотах");
        player.sendMessage("§e/ec help §7- Показать эту справку");
        
        if (player.hasPermission("enderchestec.admin")) {
            player.sendMessage("§c/ec reload §7- Перезагрузить плагин (только админы)");
        }
        
        player.sendMessage("");
        player.sendMessage("§7§oДля разблокировки новых слотов кликните по");
        player.sendMessage("§7§oзаблокированной ячейке в сундуке.");
        player.sendMessage("§7§oСтоимость: §e" + plugin.getCostPerSlot() + " алмазов §7§oза слот");
    }
    
    /**
     * Отправить информацию о слотах игрока
     */
    private void sendInfo(Player player) {
        int playerSlots = plugin.getPlayerDataManager().getPlayerSlots(player.getUniqueId());
        int maxSlots = plugin.getMaxSlots();
        int defaultSlots = plugin.getDefaultSlots();
        int costPerSlot = plugin.getCostPerSlot();
        
        player.sendMessage("§6§l=== Информация о ваших слотах ===");
        player.sendMessage("§7Доступно слотов: §e" + playerSlots + "§7/§e" + maxSlots);
        player.sendMessage("§7Базовое количество: §e" + defaultSlots);
        player.sendMessage("§7Дополнительных слотов: §e" + (playerSlots - defaultSlots));
        player.sendMessage("§7Стоимость за слот: §e" + costPerSlot + " алмазов");
        
        if (playerSlots < maxSlots) {
            int slotsToUnlock = maxSlots - playerSlots;
            int totalCost = slotsToUnlock * costPerSlot;
            player.sendMessage("");
            player.sendMessage("§7Можно разблокировать еще: §e" + slotsToUnlock + " слотов");
            player.sendMessage("§7Общая стоимость: §e" + totalCost + " алмазов");
        } else {
            player.sendMessage("");
            player.sendMessage("§a§lВы разблокировали максимальное количество слотов!");
        }
    }
    
    /**
     * Перезагрузить плагин
     */
    private void reloadPlugin(Player player) {
        try {
            // Сохраняем данные игроков
            plugin.getPlayerDataManager().savePlayerData();
            
            // Перезагружаем конфиг
            plugin.reloadConfig();
            
            player.sendMessage("§a§lПлагин EnderChestEC успешно перезагружен!");
            plugin.getLogger().info("Плагин перезагружен игроком " + player.getName());
            
        } catch (Exception e) {
            player.sendMessage("§c§lОшибка при перезагрузке плагина!");
            plugin.getLogger().severe("Ошибка при перезагрузке плагина: " + e.getMessage());
            e.printStackTrace();
        }
    }
}