package com.skyerzz.tictactoe.game.shop;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import com.skyerzz.tictactoe.game.PawnMaterial;
import com.skyerzz.tictactoe.game.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

/**
 * UI for the material menu
 * Created by sky on 2-11-2017.
 */
public class MaterialShopWindow implements Listener{

    /** Name of the Inventory */
    private final String shopName = "UTTT | Mat Shop";

    private TTTPlayer player;

    /** slots where items go */
    private final int exit=49,back=48;

    /**
     *
     * @param player TTTPlayer for which the shop is opened
     */
    public MaterialShopWindow(TTTPlayer player) {
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
            //they clicked nothing of importance
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
                if(clickPawnMaterial(PawnMaterial.getPawnMaterialByDisplayName(e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("\u00A7\\w", "")), e.getClick())){
                    SoundManager.playCachingSound(player.getPlayer());
                    open();//update the window
                }else{
                    SoundManager.playSomethingWentWrongSound(player.getPlayer());
                }
                break;
        }
    }

    /**
     * Sets a players pawnmaterial to the given input
     * @param pawnMaterial PawnMaterial that should be set
     * @param type ClickType that was used
     * @return True if succesfull, otherwise false.
     */
    private boolean clickPawnMaterial(PawnMaterial pawnMaterial, ClickType type){
        if(pawnMaterial==null){
            return false; //something went wrong i guess
        }
        if(player.getPrimaryMaterial() == pawnMaterial.getMaterial() && player.getPrimaryMaterialData()==pawnMaterial.getMaterialData()){
            //they already have this pawn selected as primary pawn
            return false;
        }
        if(player.getSecondaryMaterial() == pawnMaterial.getMaterial() && player.getSecondaryMaterialData()==pawnMaterial.getMaterialData()){
            //they already have this pawn selected as secondary pawn
            return false;
        }
        //lets select this pawn for them!
        if(type == ClickType.LEFT) {
            player.setPrimaryPawnMaterial(pawnMaterial);
        }else if(type == ClickType.RIGHT){
            player.setSecondaryPawnMaterial(pawnMaterial);
        }else{
            //idk what they did but they clicked a wrong button.
            player.getPlayer().sendMessage("\u00A7cTry clicking left or right mouse button instead!");
            return false;
        }
        return true;
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
     * Builds an Inventory for the Material Shop
     * @return Inventory player sees
     */
    private Inventory getScreen() {
        Inventory screen = Bukkit.createInventory(null, 54, shopName);
        //set default items
        screen.setItem(exit, GeneralShopItem.getQuitItem());
        screen.setItem(back, GeneralShopItem.getPreviousMenuItem());

        int index = 10;
        for(PawnMaterial pawnMaterial:PawnMaterial.values()){
            screen.setItem(index++, GeneralShopItem.getItemFromPawnMaterial(player, pawnMaterial));

            if((index+1)%9==0){
                //+2 to leave a space on the left and right sides of the inventory
                index+=2;
            }
        }

        return screen;
    }


}
