package com.skyerzz.tictactoe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main Plugin class
 * Created by sky on 7-8-2017.
 */
public class TicTacToe extends JavaPlugin {

    /**
     * Plugin Instance, used in the entire project to get Events registered
     */
    public static TicTacToe pluginInstance;
    public static final String version = "1.0";

    @Override
    public void onEnable() {
        getLogger().info("Booting up Ultimate Tic Tac toe version " + version);
        pluginInstance = this;

        getLogger().info("Spawning NPC...");
        new TTTNPC(new Location(Bukkit.getWorlds().get(0), 7.5, 18.5, 3.5, 114.5f, 0f));

        getLogger().info("Setting command...");
        this.getCommand("tictactoe").setExecutor(new TicTacToeCommand());
        this.getCommand("tictactoe").setTabCompleter(new TicTacToeCommandTabCompleter());

        getLogger().info("has been Enabled!");

    }

    @Override
    public void onDisable(){
        getLogger().info("Killing NPC's...");
        TTTNPC.killAllInstances();
        getLogger().info("Interrupting Games...");
        TicTacToeCommand.interruptAll();
        getLogger().info("Shutting down...");
    }
}
