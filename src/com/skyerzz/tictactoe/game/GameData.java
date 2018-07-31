package com.skyerzz.tictactoe.game;

import com.google.gson.JsonObject;

import java.util.UUID;

/**
 * Data of a played game, used to save statistics.
 * Created by sky on 23-10-2017.
 */
public class GameData {

    private TTTBoard board;

    public GameData(TTTBoard board){
        this.board = board;
    }

    /**
     *
     * @return UUID of the winning player, or null in case of a tied match
     */
    public UUID getWinner(){
        return board.getWinner() == 1 ?
                    board.getCrossPlayer().getPlayer().getUniqueId() :
                board.getWinner() == 2 ?
                    board.getCirclePlayer().getPlayer().getUniqueId() :
                null;
    }

    /**
     * Counts the total moves the game has done
     * @return total moves the game had
     */
    public int getTotalMoves(){
        return board.getTotalMoves();
    }

    /**
     * Gets the current time, and subtracts the start time
     * @return time the game lasted in milliseconds
     */
    public long getTimePlayed(){
        return System.currentTimeMillis() - board.getStartTime();
    }

    /**
     * Creates and returns a jsonObject for the GameData
     * @return class as JsonObject
     */
    public JsonObject getAsJsonObject(){
        JsonObject json = new JsonObject();
        json.addProperty("winner", getWinner() == null ? "tied": getWinner().toString());
        json.addProperty("moves", getTotalMoves());
        json.addProperty("time", getTimePlayed());
        return json;
    }
}
