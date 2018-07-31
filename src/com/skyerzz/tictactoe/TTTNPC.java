package com.skyerzz.tictactoe;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Our villager in the lobby
 * Created by sky on 8-10-2017.
 */
public class TTTNPC implements Listener{

    private static List<TTTNPC> instances = new ArrayList<>();

    private Villager villager;

    /**
     * Spawns a new VillagerNPC for TTT in the given location
     * @param location Location to spawn the villagerNPC
     */
    public TTTNPC(Location location){
        //spawn a new villager (librarian)
        this.villager = (Villager) Bukkit.getWorlds().get(0).spawnEntity(location, EntityType.VILLAGER);
        villager.setProfession(Villager.Profession.LIBRARIAN);

        //give him a name and stuffs
        Entity nmsEntity = ((CraftEntity) villager).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        tag.setInt("Silent", 1);
        tag.setBoolean("Invulnerable", true); //THIS DOESNT SEEM TO WORK IN ANY FORMAT.
        nmsEntity.f(tag);
        villager.setCustomName("Ultimate Tic Tac Toe");
        villager.setCustomNameVisible(true);
        Bukkit.getServer().getPluginManager().registerEvents(this, TicTacToe.pluginInstance);
        villager.teleport(villager.getLocation()); //for some reason this is needed to make them face the right way...

        //add it as an instance
        TTTNPC.instances.add(this);
    }

    /**
     * Stops our villager from taking damage.
     * Make-shift instead, cause setting invulnerable doesnt seem to work.
     * @param e EntityDamagebyEntityEvent
     */
    @EventHandler
    public void onNPCTakeDamage(EntityDamageByEntityEvent e){
        if(e.getEntity().getUniqueId() == villager.getUniqueId()){
            e.setCancelled(true);
            if(e.getDamager() instanceof Player){
                ((Player) e.getDamager()).performCommand("ttt shop main");
            }
        }
    }

    /**
     * Opens the shop when someone clicks our villagerNPC
     * @param e PlayerInteractEntityEvent
     */
    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent e){
        if(e.getRightClicked().getUniqueId() != villager.getUniqueId()){
            //not our villager
            return;
        }
        e.setCancelled(true); //not that shop...
        //they clicked it, lets give em the shop!
        e.getPlayer().performCommand("ttt shop main");
    }

    /**
     * Unregister everything if our villagerNPC dies (or gets removed)
     * @param e EntityDeathEvent
     */
    @EventHandler
    public void onNPCDeath(EntityDeathEvent e){
        if(e.getEntity().getUniqueId() != villager.getUniqueId()){
            return;
        }
        //OUR VILLAGER DIED D:
        unregister();
    }

    /**
     * Unregisters all listeners
     */
    public void unregister(){
        PlayerInteractEntityEvent.getHandlerList().unregister(this);
        EntityDeathEvent.getHandlerList().unregister(this);

    }

    /**
     * Kills off our villagerNPC
     */
    public void kill(){
        villager.remove();
        unregister();
    }

    /**
     * Kills ALL villagerNPC's for Tic Tac Toe.
     */
    public static void killAllInstances(){
        instances.forEach(TTTNPC::kill);
    }
}
