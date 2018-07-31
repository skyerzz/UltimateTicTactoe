package com.skyerzz.tictactoe.game.shop;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

/**
 * UI of the Challenge window
 * Created by sky on 8-10-2017.
 */
public class ChallengeShopWindow implements Listener {

    /** Name of the Inventory */
    private final String shopName = "UTTT Challenge!";
    private TTTPlayer player;

    /** slots where items go */
    private final int exit=49,back=48,error=22;

    /**
     *
     * @param player TTTPlayer for which the shop is opened
     */
    public ChallengeShopWindow(TTTPlayer player) {
        this.player =player;
    }

    /**
     * Quick Shutdown function, which will close and exit the shop window
     */
    private void shutDown(){
        shutDown(true);
    }

    //todo check why you have a shutdown() and a shutdown(true) if shutdown(false) doesnt do anything
    /**
     * Closes the inventory window if it matches the current open window
     * @param closeWindow Boolean if the inventory should be closed.
     */
    private void shutDown(boolean closeWindow){
        if(player.getPlayer().getOpenInventory().getTitle().equalsIgnoreCase(this.shopName) && closeWindow){
            player.getPlayer().closeInventory();
        }
    }

    /**
     * Opens the shop for the player
     * Registers the events used in this class
     */
    public void open(){
        player.getPlayer().openInventory(getScreen());
        Bukkit.getServer().getPluginManager().registerEvents(this, TicTacToe.pluginInstance);
    }

    /**
     * Triggers when any inventory is clicked
     * @param e InventoryClickEvent
     */
    @EventHandler
    void onInventoryClick(InventoryClickEvent e){
        if(e.getWhoClicked().getUniqueId() != player.getPlayer().getUniqueId()){
            //its not our player, get out!
            return;
        }
        //its our player <3
        if(e.getClickedInventory() == null){
            //someone clicked outside the inventory, dont do anything
            return;
        }
        String shopName = e.getClickedInventory().getName();
        if(!shopName.equals(this.shopName)){
            //its not our shop :( OR the shop didnt exist
            return;
        }
        e.setCancelled(true); //we dont want m doing anything of course!

        //lets see where they clicked, and cross-match it with our slots. Do something depending on what they did.
        switch(e.getSlot()){
            case exit:
                shutDown();
                break;
            case back:
                player.getPlayer().performCommand("ttt shop main");
                shutDown();
                break;
            default:
                if(e.getCurrentItem()==null){
                    break;//cant do anything with a null item. They probably clicked on an empty slot
                }
                //get the Player they clicked on, minus formatting through replaceAll
                String playerName = e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\u00A7\\w", "");
                if(playerName==null || playerName.isEmpty()){
                    //guess they clicked something we dont know? Should never be fired. Output debug statement if this ever happens
                    System.out.println("[TTT] ERROR playerNull: " + e.getCurrentItem().getItemMeta().getDisplayName());
                    break;
                }
                //send the challenged player a challenge, then shut down the challenge window
                challengePlayer(playerName);
                shutDown();
                break;
        }
    }

    /**
     * Sends a challenge command to the given player
     * @param playerName The challenged player
     */
    private void challengePlayer(String playerName){
        player.getPlayer().performCommand("ttt invite " + playerName);
    }

    /**
     * Triggers when any inventory is closed
     * @param e InventoryCloseEvent
     */
    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e){
        if(e.getPlayer().getUniqueId() != player.getPlayer().getUniqueId()){
            //its not our player, get out!
            return;
        }
        if(!e.getInventory().getName().equals(this.shopName)){
            //not our inventory
            return;
        }
        //our player closed our inventory :( Lets shut it down, and unregister any events so they wont be triggered twice in the future
        shutDown(false);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    /**
     * Creates a new inventory for Challenges
     * @return Inventory player sees
     */
    private Inventory getScreen(){
        Inventory screen = Bukkit.createInventory(null, 54, shopName);
        //set default items
        screen.setItem(exit, GeneralShopItem.getQuitItem());
        screen.setItem(back, GeneralShopItem.getPreviousMenuItem());

        //set all online players in there
        int index = 10;
        for(Player p : Bukkit.getOnlinePlayers()){
            if(this.player.getPlayer().getUniqueId()==p.getUniqueId()){
                //we dont want to set our own head of course.
                continue;
            }

            screen.setItem(index, getPlayerHead(p));

            if(++index%9==0){
                //leave a space at the left and right columns
                index+=2;
            }
        }
        if(index==10){
            //no players were visible. Show the Error pane instead.
            screen.setItem(error, GeneralShopItem.getErrorItem("No online players!"));
        }

        return screen;
    }

    /**
     *
     * @param player PlayerName who's head is being made
     * @return ItemStack of a playerHead
     */
    private ItemStack getPlayerHead(Player player){
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(player.getName());
        meta.setDisplayName("\u00A77" + player.getName());
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("Click to challenge " + player.getName() + " to a game!");
        meta.setLore(lore);
        head.setItemMeta(meta);
        return head;
    }
}
