package com.github.jackspawner;

import com.github.jackspawner.commands.GiveJackCommand;
import com.github.jackspawner.listeners.BlockBreakListener;
import com.github.jackspawner.listeners.BlockPlaceListener;
import org.bukkit.plugin.java.JavaPlugin;

public class JackSpawnerPlugin extends JavaPlugin {
    
    private static JackSpawnerPlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Register command
        getCommand("givejack").setExecutor(new GiveJackCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        
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