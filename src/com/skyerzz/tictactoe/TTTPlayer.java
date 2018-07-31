package com.skyerzz.tictactoe;

import com.skyerzz.tictactoe.game.GameData;
import com.skyerzz.tictactoe.game.Pawn;
import com.skyerzz.tictactoe.game.PawnMaterial;
import com.skyerzz.tictactoe.game.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This is the player
 * Created by sky on 5-9-2017.
 */
public class TTTPlayer {

    private Player player;
    private PlayerData playerData;

    /**
     * Instance a TTT Player
     * @param player Player to be instanced into TTTPlayer
     */
    public TTTPlayer(Player player){
        this.player = player;
        this.playerData = new PlayerData(player.getUniqueId());
    }

    /**
     *
     * @return the player object
     */
    public Player getPlayer(){
        return player;
    }

    /**
     * Gets the X-type pawn
     * @return Pawn of type X
     */
    public Pawn getPrimaryPawn(){
        return playerData.getPrimaryPawn();
    }

    /**
     * Gets the O-type pawn
     * @return Pawn of type O
     */
    public Pawn getSecondaryPawn(){
        return playerData.getSecondaryPawn();
    }

    /**
     * Gets the current pawn, based on the pawn of the opponent.
     * Will return primary pawn if the opponents pawn is not the same, otherwise secondary pawn.
     * @param otherUserPawn Pawn of the other user
     * @return Pawn of the given type
     */
    public Pawn getPawn(Pawn otherUserPawn){
        return getPrimaryPawn()==otherUserPawn ? getSecondaryPawn() : getPrimaryPawn();
    }

    /**
     * Sets the X-type pawn of a player to Pawn
     * @param p Pawn of type X to set for player
     */
    public void setPrimaryPawn(Pawn p){
        playerData.setPrimaryPawn(p);
    }

    /**
     * Sets the O-Type pawn of a player to Pawn
     * @param p Pawn of type O to set for player
     */
    public void setSecondaryPawn(Pawn p){
        playerData.setSecondaryPawn(p);
    }

    /**
     * Adds the amount of coins to a player's balance
     * @param amount amount of coins to add
     */
    public void addCoins(int amount){
        playerData.setIntValue("coins", getCoins() + amount);
    }

    /**
     * Sets the player's balance to the amount of coins
     * @param amount Amount of coins to set to a player
     */
    public void setCoins(int amount) { playerData.setIntValue("coins", amount);}

    /**
     * Tries to remove the amount of coins from a player. Returns false if player doesnt have enough coins
     * @param amount Amount of coins to remove from the player's balance
     * @return True if successfull, false if the player didnt have enough coins
     */
    public boolean removeCoins(int amount){
        if(amount > getCoins()){
            return false;
        }
        addCoins(amount*-1);
        return true;
    }

    public int getIntValue(String key){
        return playerData.getIntValue(key);
    }

    /**
     * Adds an unlocked pawn to the player
     * @param p Pawn to unlock for the player
     */
    public void addPawn(Pawn p){
        playerData.addPawn(p);
    }

    /**
     * Gets the amount of coins from a player
     * @return Amount of coins a player has
     */
    public int getCoins(){ return playerData.getIntValue("coins"); }

    /**
     * Returns a boolean value to indicate if a player has the given pawn
     * @param p Pawn to check
     * @return True if player has the pawn, false if player doesnt have the pawn
     */
    public boolean hasPawn(Pawn p){
        for(Pawn pawn: playerData.getUnlockedPawns()){
            if(pawn==p){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Material for the pawns
     * @return Material for the pawns
     */
    public Material getPrimaryMaterial(){
        return playerData.getPrimaryMaterial();
    }

    /**
     * Returns the Material Data for the pawn blocks
     * @return byte MaterialData
     */
    public byte getPrimaryMaterialData(){
        return playerData.getPrimaryMaterialData();
    }
    
    /**
     * Returns the Material for the pawns
     * @return Material for the pawns
     */
    public Material getSecondaryMaterial(){
        return playerData.getSecondaryMaterial();
    }

    /**
     * Returns the Material Data for the pawn blocks
     * @return byte MaterialData
     */
    public byte getSecondaryMaterialData(){
        return playerData.getSecondaryMaterialData();
    }

    /**
     * Adds a completed game's stats to the player's data
     * @param data GameData from a completed game
     */
    public void addGameData(GameData data){
        playerData.addGameInstance(data);
        if(data.getWinner()==player.getUniqueId()){
            playerData.setIntValue("wins", playerData.getIntValue("wins")+1);
        }else if(data.getWinner()==null){
            playerData.setIntValue("draws", playerData.getIntValue("draws")+1);
        }else{
            playerData.setIntValue("losses", playerData.getIntValue("losses")+1);
        }
    }

    /**
     * sets primary pawn material
     * @param pawnMaterial PawnMaterial
     */
    public void setPrimaryPawnMaterial(PawnMaterial pawnMaterial){
        playerData.setPrimaryMaterial(pawnMaterial.getMaterial());
        playerData.setPrimaryMaterialData(pawnMaterial.getMaterialData());
    }
    
    /**
     * sets secondary pawn material
     * @param pawnMaterial PawnMaterial
     */
    public void setSecondaryPawnMaterial(PawnMaterial pawnMaterial){
        playerData.setSecondaryMaterial(pawnMaterial.getMaterial());
        playerData.setSecondaryMaterialData(pawnMaterial.getMaterialData());
    }

    /**
     * Returns a pawn material different from the opponents pawn. Primary or secondary, in that order.
     * @param opponentPawn Pawn from the other person
     * @return PawnMaterial to pick
     */
    public Material getPawnMaterial(Material opponentPawn, byte materialData){
        if(opponentPawn == getPrimaryMaterial() && materialData == getPrimaryMaterialData()){
            return getSecondaryMaterial();
        }
        return getPrimaryMaterial();
    }

    /**
     * Returns a pawn material data different from the opponents pawn. Primary or secondary, in that order.
     * @param opponentPawn Pawn from the other person
     * @return PawnMaterialData to pick
     */
    public byte getPawnMaterialData(Material opponentPawn, byte materialData){
        if(opponentPawn == getPrimaryMaterial() && materialData == getPrimaryMaterialData()){
            return getSecondaryMaterialData();
        }
        return getPrimaryMaterialData();
    }
}
