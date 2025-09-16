package com.github.jackspawner;

import com.github.jackspawner.commands.GiveJackCommand;
import com.github.jackspawner.listeners.BlockBreakListener;
import com.github.jackspawner.listeners.BlockPlaceListener;
import com.github.jackspawner.listeners.ShopClickListener;
import com.github.jackspawner.commands.JackShopCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class JackSpawnerPlugin extends JavaPlugin {
    
    private static JackSpawnerPlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Register commands
        getCommand("givejack").setExecutor(new GiveJackCommand());
        getCommand("jackshop").setExecutor(new JackShopCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new ShopClickListener(), this);
        
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