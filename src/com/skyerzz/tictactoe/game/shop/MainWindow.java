package com.skyerzz.tictactoe.game.shop;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import com.skyerzz.tictactoe.game.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * UI for the main menu
 * Created by sky on 7-10-2017.
 */
public class MainWindow implements Listener{

    /** Name of the Inventory */
    private final String shopName = "Ultimate Tic Tac Toe";
    private TTTPlayer player;

    /** slots where items go */
    private final int exit=49,pawns=12,boards=-1,challenge=31,material=14,tutorial=28,statistics=34;

    /**
     *
     * @param player TTTPlayer for which the inventory is being made
     */
    public MainWindow(TTTPlayer player) {
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
            //someone clicked outside the inventory
            return;
        }
        String shopName = e.getClickedInventory().getName();
        if(!shopName.equals(this.shopName)){
            //its not our shop :( OR the shop didnt exist
            return;
        }
        e.setCancelled(true); //we dont want m doing anything of course!
        switch(e.getSlot()){
            case exit:
                shutDown();
                break;
            case pawns:
                player.getPlayer().performCommand("ttt shop pawns");
                shutDown();
                break;
            case challenge:
                player.getPlayer().performCommand("ttt shop challenge");
                shutDown();
                break;
            case boards:
                SoundManager.playSomethingWentWrongSound(player.getPlayer());
                //do nothing, soonTM
                break;
            case material:
                player.getPlayer().performCommand("ttt shop material");
                shutDown();
                break;
            case tutorial:
                player.getPlayer().performCommand("ttt tutorial");
                break;
            case statistics:
                //dont do anything.
                break;
        }
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
     * Builds a new inventory for the player
     * @return Inventory player sees
     */
    private Inventory getScreen(){
        Inventory screen = Bukkit.createInventory(null, 54, shopName);
        //set all items in the defined spots
        screen.setItem(exit, GeneralShopItem.getQuitItem());

        screen.setItem(pawns, GeneralShopItem.getOPawnShopItem());

        //screen.setItem(boards, GeneralShopItem.getBoardShopItem());

        screen.setItem(tutorial, GeneralShopItem.getHowToPlayBook());

        screen.setItem(statistics, GeneralShopItem.getStatisticsPage(player));

        screen.setItem(material, GeneralShopItem.getMaterialShopItem());

        screen.setItem(challenge, GeneralShopItem.getChallengeShopItem());

        return screen;
    }


}
