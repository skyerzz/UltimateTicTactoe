package com.skyerzz.tictactoe.game.shop;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import com.skyerzz.tictactoe.game.Pawn;
import com.skyerzz.tictactoe.game.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * UI of the Pawn window
 * Created by sky on 7-10-2017.
 */
public class PawnShopWindow implements Listener{

    /** Name of the Inventory */
    private final String shopName = "UTTT | Pawn Shop";

    private TTTPlayer player;

    /** slots where items go */
    private final int exit=49,back=48;

    /**
     *
     * @param player TTTPlayer for which the shop is opened
     */
    public PawnShopWindow(TTTPlayer player) {
        this.player = player;
    }

    /**
     * Quick Shutdown function, which will close and exit the shop window
     */
    private void shutDown(){
        shutDown(true);
    }

    //todo check why you have a shutdown() and a shutdown(true) if shutdown(false) doesnt do anything. Not yet removed cause im afraid itll break stuff otherwise.
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
    private void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked().getUniqueId() != player.getPlayer().getUniqueId()){
            //its not our player, get out!
            return;
        }
        //its our player <3
        if(e.getClickedInventory()==null){
            //its outside of the inventory window
            return;
        }
        String shopName = e.getClickedInventory().getName();
        if(!shopName.equals(this.shopName)){
            //its not our shop :(
            return;
        }
        if(e.getCurrentItem()==null || e.getCurrentItem().getItemMeta()==null || e.getCurrentItem().getItemMeta().getDisplayName()==null){
            //they clicked nothing
            return;
        }
        e.setCancelled(true); //we dont want m doing anything of course!
        //lets see what they clicked
        switch(e.getSlot()){
            case exit:
                shutDown();
                break;
            case back:
                player.getPlayer().performCommand("ttt shop main");
                shutDown();
                break;
            default:
                //get the Pawn they clicked on
                Pawn clickedPawn = Pawn.getPawnByDisplayName(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\u00A7\\w", ""));
                if(clickedPawn==null){
                    //guess they clicked something we dont know. Should never happen, but if it does, print a debug statement
                    System.out.println("[TTT] ERROR pawnNull: " + e.getCurrentItem().getItemMeta().getDisplayName());
                    break;
                }
                //if they did something that should update the window, update the window.
                if(clickedPawn(clickedPawn, e.getClick())){
                    open();//update the window
                }
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
        //our player closed our inventory :( Lets shut it down and unregister
        shutDown(false);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
    }

    /**
     * Builds an Inventory for the Pawn shop, depending if they clicked on O or X pawns
     * @return Inventory player sees
     */
    private Inventory getScreen() {
        Inventory screen = Bukkit.createInventory(null, 54, shopName);
        //set default items
        screen.setItem(exit, GeneralShopItem.getQuitItem());
        screen.setItem(back, GeneralShopItem.getPreviousMenuItem());

        int index = 10;
        for(Pawn p: Pawn.values()){
            screen.setItem(index, GeneralShopItem.getItemFromPawn(player, p));

            if(++index%9==0){
                //+2 to leave a space on the left and right sides of the inventory
                index+=2;
            }
        }

        return screen;
    }

    /**
     *
     * @param pawn Pawn that got clicked
     * @return true if something changed, false if nothing changed
     */
    private boolean clickedPawn(Pawn pawn, ClickType clickType){
        if(player.hasPawn(pawn)){
            //the player already has the pawn, check if they select it.
            if(player.getPrimaryPawn()==pawn || player.getSecondaryPawn()==pawn){
                SoundManager.playSomethingWentWrongSound(player.getPlayer());
                player.getPlayer().sendMessage("\u00A7cYou already have selected this pawn!");
                return false;
            }
            //they selected this pawn! Lets set it correctly in their PlayerData
            if(clickType==ClickType.LEFT){
                player.setPrimaryPawn(pawn);
            }else if(clickType==ClickType.RIGHT){
                player.setSecondaryPawn(pawn);
            }else{
                SoundManager.playSomethingWentWrongSound(player.getPlayer());
                player.getPlayer().sendMessage("\u00A7cI have no idea what button you pressed. Try right or left click.");
                return false;
            }
            //nice sound to make it complete :3
            SoundManager.playCachingSound(player.getPlayer());
            return true;
        }
        //the player didnt have the pawn yet. Check if they can buy it
        if(!player.removeCoins(pawn.getPrice())){
            //They didnt have enough coins! Play sad violin and tell them to bugger off
            SoundManager.playSomethingWentWrongSound(player.getPlayer());
            player.getPlayer().sendMessage("\u00A7cYou do not have enough coins to buy this!");
            return false;
        }
        //they had enough coins, which were removed in the removeCoins statement! Lets give them the pawn now too :)
        buyPawn(pawn);
        SoundManager.playCachingSound(player.getPlayer());
        return true;
    }

    /**
     * Adds the bought pawn to the playerData
     * @param pawn Pawn player bought
     */
    private void buyPawn(Pawn pawn){
        player.addPawn(pawn);
    }


}
