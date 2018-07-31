package com.skyerzz.tictactoe.game.shop;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.book.BookBuilder;
import com.skyerzz.tictactoe.book.Page;
import com.skyerzz.tictactoe.game.Pawn;
import com.skyerzz.tictactoe.game.PawnMaterial;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Holder for items used in UI's
 * Created by sky on 8-10-2017.
 */
public class GeneralShopItem {


    /**
     * Returns an itemstack for the statistics page
     * @param player Player to generate statistics for
     * @return ItemStack for the Statistics page
     */
    public static ItemStack getStatisticsPage(TTTPlayer player){
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName("\u00A72" + player.getPlayer().getName() + "'s Statistics");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Wins: " + player.getIntValue("wins"));
        lore.add("Draws: " + player.getIntValue("draws"));
        lore.add("Losses: " + player.getIntValue("losses"));
        meta.setLore(lore);
        paper.setItemMeta(meta);
        return paper;
    }

    /**
     * Builds the How-to-play book for Tic Tac Toe
     */
    public static ItemStack getHowToPlayBook(){
        BookBuilder book = new BookBuilder("How to Play", "skyerzz");

        Page page = new Page();
        page.addText("      The Goal\n", ChatColor.RED, Page.Property.BOLD);
        page.addNewLines(1);
        page.addText("Just like normal Tic Tac Toe, the goal is to get 3 in a row horizontally, vertically or diagonally.\n" +
                "The game ends when someone has 3 in a row on the large boards, or no more moves can be made.\n");
        page.addNewLines(1);
        page.addText("    How to play >>>", ChatColor.RED);
        book.addPage(page);

        page = new Page();
        page.addText("      How to Play\n", ChatColor.RED, Page.Property.BOLD);
        page.addNewLines(1);
        page.addText("The arena exists of 3*3 smaller boards of 9 fields. When a player makes a move in a smaller field, the opponent gets forced to make the next move on the smaller board that mirrors the square of the player's move.\n");
        page.addText("           >>>");
        book.addPage(page);

        page = new Page();
        page.addNewLines(1);
        page.addText("Right or left click to select your move at the spot you are looking at! Particles will indicate where you are placing your move. If you get forced to a board which has no possible moves left, you get to freely pick where you place your move.");
        book.addPage(page);


        return book.build();
    }

    /**
     *
     * @param player Player the pawnMaterial is built for
     * @param pawnMaterial PawnMaterial that is currently being built
     * @return ItemStack for the PawnMaterial shop icon
     */
    public static ItemStack getItemFromPawnMaterial(TTTPlayer player, PawnMaterial pawnMaterial){
        ItemStack item = new ItemStack(pawnMaterial.getMaterial(), 1 ,pawnMaterial.getMaterialData());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72" + pawnMaterial.getDisplayName());
        ArrayList<String> lore = new ArrayList<>();
        if(player.getPrimaryMaterial()==pawnMaterial.getMaterial() && player.getPrimaryMaterialData() == pawnMaterial.getMaterialData()) {
            lore.add("\u00A72Selected as Primary Material!");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }else if(player.getSecondaryMaterial()==pawnMaterial.getMaterial() && player.getSecondaryMaterialData() == pawnMaterial.getMaterialData()){
            lore.add("\u00A72Selected as Secondary Material!");
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }else{
            lore.add("\u00A77Left-Click to select as Primary Material!");
            lore.add("\u00A77Right-Click to select as Secondary Material!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Unused method for getting the X-pawns shop item.
     * @return ItemStack for the X-pawns shop icon
     */
    public static ItemStack getXPawnShopItem(){
        ItemStack item = new ItemStack(Material.WOOD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72X-Pawns");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("\u00A77Click to shop/select X-Pawns!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @return ItemStack for the O-pawns shop icon
     */
    public static ItemStack getOPawnShopItem(){
        ItemStack item = new ItemStack(Material.WOOD, 1);
        item.setDurability((short) 2);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72Pawns");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("\u00A77Click to shop/select Pawns!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @return ItemStack for the PawnMaterial shop icon
     */
    public static ItemStack getMaterialShopItem(){
        ItemStack item = new ItemStack(Material.INK_SACK, 1, (byte) 7);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72Pawn Materials");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("\u00A77Click to shop/select Pawn Materials!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *  Unused method. can be used soon
     * @return ItemStack for the Board shop icon
     */
    public static ItemStack getBoardShopItem(){
        ItemStack item = new ItemStack(Material.STAINED_CLAY, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72Boards");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("\u00A7cComing Soon\u0054\u004D");
        //lore.add("\u00A77Click to shop/select Boards!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @return ItemStack for the challenge shop icon
     */
    public static ItemStack getChallengeShopItem(){
        ItemStack item = new ItemStack(Material.IRON_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A72Play");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("\u00A7aClick to Play!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @return ItemStack for the Quit shop item
     */
    public static ItemStack getQuitItem(){
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A7cExit");
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @return ItemStack for the Previous Menu shop item
     */
    public static ItemStack getPreviousMenuItem(){
        ItemStack item = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A7cGo Back");
        item.setItemMeta(meta);
        return item;
    }

    /**
     *
     * @param error String ErrorMessage
     * @return ItemStack for an Error item in the shop
     */
    public static ItemStack getErrorItem(String error){
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A7c" + error);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an Item for a specific pawn, with different text depending if the supplied player has or has not unlocked the pawn
     * @param player the TTTPlayer who has the shop opened
     * @param p the Pawn the ItemStack is asked from
     * @return ItemStack for the requested Pawn
     */
    public static ItemStack getItemFromPawn(TTTPlayer player, Pawn p){
        ItemStack item = new ItemStack(p.getMaterial(), 1, p.getMaterialData());
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        if(player.hasPawn(p)){
            meta.setDisplayName("\u00A7a"+p.getDisplayName());
            lore.add("\u00A7aUnlocked!");
        }else{
            meta.setDisplayName("\u00A7c"+p.getDisplayName());
            lore.add("\u00A77Cost:\u00A76 "+p.getFormattedPrice());
            lore.add("\u00A7cClick to unlock!");
        }
        if(player.getPrimaryPawn()==p){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            lore.add("\u00A7aSelected as Primary Pawn!");
        }else if(player.getSecondaryPawn()==p){
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            lore.add("\u00A7aSelected as Secondary Pawn!");
        }else if(player.hasPawn(p)){
            lore.add("\u00A7aLeft-Click to select as Primary Pawn!");
            lore.add("\u00A7aRight-Click to select as Secondary Pawn!");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
