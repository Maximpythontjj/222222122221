package ru.jackspawner;

import org.bukkit.plugin.java.JavaPlugin;
import ru.jackspawner.commands.GiveJackCommand;
import ru.jackspawner.listeners.BlockBreakListener;
import ru.jackspawner.listeners.BlockPlaceListener;

public final class JackSpawnerPlugin extends JavaPlugin {

    private static JackSpawnerPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        
        // Регистрируем слушатели событий
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        
        // Регистрируем команды
        getCommand("givejack").setExecutor(new GiveJackCommand());
        
        getLogger().info("JackSpawner enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("JackSpawner disabled");
    }
    
    public static JackSpawnerPlugin getInstance() {
        return instance;
    }
}