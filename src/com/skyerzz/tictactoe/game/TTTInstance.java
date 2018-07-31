package com.skyerzz.tictactoe.game;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * 1 3*3 board (9 instances per game)
 * Created by sky on 7-8-2017.
 */
class TTTInstance {


    private int[][] field;
    private int x,y,z;
    private int winner = 0;//0=nobody,1=x,2=o
    private int particles;
    private TTTPlayer pCross, pCircle;

    /**
     *
     * @param x top-left x location
     * @param y top-left y location
     * @param z top-left z location
     * @param pCross player One (X / cross)
     * @param pCircle player Two (O / circle)
     */
    public TTTInstance(int x, int y, int z, TTTPlayer pCross, TTTPlayer pCircle){
        field = new int[3][3];
        this.x = x;
        this.y = y;
        this.z = z;
        this.pCross = pCross;
        this.pCircle = pCircle;
    }

    /**
     *
     * @param row Row of the board
     * @param column Column of the board
     * @param input who's move it is (true = player pCross, false = player pCircle)
     * @param pawn Pawn that is used in the move
     * @return True if successful, false if unsuccessful
     */
    public boolean move(int row, int column, boolean input, Pawn pawn){
        if(isDone()){
            //this board is already done
            return false;
        }
        if(field[row][column]!=0){
            //theres already a pawn there!
            return false;
        }
        field[row][column] = input ? 1 : 2;
        //draw the pawn on the slot specified
        draw(row, column, input);
        if(isDone()){
            //if its done, draw a winner for this board
            drawWinner(pawn);
        }
        return true;
    }

    /**
     * Copy's the players pawn to the board position
     * @param row Row of the board
     * @param column Column of the board
     * @param input Who's move it is (true = player pCross, false =  player pCircle)
     */
    private void draw(int row, int column, boolean input){
        int x = this.x+(10*column);
        int y = this.y-(10*row);
        if(input){
            pCross.getPrimaryPawn().copy(x, y, this.z, 0, pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData());
        }else{
            pCircle.getPawn(pCross.getPrimaryPawn()).copy(x, y, this.z, 0, pCircle.getPawnMaterial(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()), pCircle.getPawnMaterialData(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()));
        }
    }

    /**
     * Returns a boolean value to represent if a square is taken (true) or still available (false)
     * @param row Row of the board
     * @param column Column of the board
     * @return true if it is set, false if its not set
     */
    private boolean isSet(int row, int column){
        return field[row][column]!=0;
    }

    /**
     * Shows the particles for a pawn for a player on the board if it can be placed there
     * @param row row of the particles
     * @param column column of the particles
     * @param player Player to display it for
     */
    public void squareParticles(int row, int column, TTTPlayer player){
        if(isSet(row, column)) {
            //dont show the option if its not available!
            return;
        }
        //middle
        int i = TTTInstance.this.x + (10 * column);
        int j = TTTInstance.this.y - (10 * row);
        if(player==pCross) {
            pCross.getPrimaryPawn().showParticles(i, j, this.z, ParticleColor.GREEN, player);
        }else{
            pCircle.getPawn(pCross.getPrimaryPawn()).showParticles(i, j, this.z, ParticleColor.GREEN, player);
        }
    }

    /** particles around the active board */
    /**
     *
     * @param on Boolean value if the particles should go on or off
     * @param one First Player (green color)
     * @param two Second Player (red color)
     */
    public void setParticles(boolean on, TTTPlayer one, TTTPlayer two, ArrayList<Player> specs){
        if(on && !isDone()){

            //create a new task that repeats every tick
            particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(TicTacToe.pluginInstance, new Runnable() {

                //instance the first coords
                float i = TTTInstance.this.x;
                float j = TTTInstance.this.y+1;

                @Override
                public void run() {
                    for(float q = -0.5f; q < 0.5; q+=0.1) {
                        ParticleColor red = ParticleColor.RED;
                        ParticleColor green =ParticleColor.GREEN;
                        //send both players a green or red particle square to indicate the active board (and if its their turn)
                        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, i, TTTInstance.this.y + 1 + q, TTTInstance.this.z + 0.5f, green.getOne(), green.getTwo(), green.getThree(), 1, 0, 0);
                        ((CraftPlayer) one.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        for(Player p:specs){
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                        }
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, i, TTTInstance.this.y + 1 + q, TTTInstance.this.z + 0.5f, red.getOne(), red.getTwo(), red.getThree(), 1, 0, 0);
                        ((CraftPlayer) two.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, TTTInstance.this.x + q, TTTInstance.this.y - 28 + (TTTInstance.this.y - j), TTTInstance.this.z + 0.5f, green.getOne(), green.getTwo(), green.getThree(), 1, 0, 0);
                        ((CraftPlayer) one.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        for(Player p:specs){
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                        }
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, TTTInstance.this.x + q, TTTInstance.this.y - 28 + (TTTInstance.this.y - j), TTTInstance.this.z + 0.5f, red.getOne(), red.getTwo(), red.getThree(), 1, 0, 0);
                        ((CraftPlayer) two.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,  TTTInstance.this.x + 29 + q, j, TTTInstance.this.z + 0.5f, green.getOne(), green.getTwo(), green.getThree(), 1, 0, 0);
                        ((CraftPlayer) one.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        for(Player p:specs){
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                        }
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,  TTTInstance.this.x + 29 + q, j, TTTInstance.this.z + 0.5f, red.getOne(), red.getTwo(), red.getThree(), 1, 0, 0);
                        ((CraftPlayer) two.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,  TTTInstance.this.x + 29 - (i - TTTInstance.this.x), TTTInstance.this.y - 28 + q, TTTInstance.this.z + 0.5f, green.getOne(), green.getTwo(), green.getThree(), 1, 0, 0);
                        ((CraftPlayer) one.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                        for(Player p:specs){
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                        }
                        packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,  TTTInstance.this.x + 29 - (i - TTTInstance.this.x), TTTInstance.this.y - 28 + q, TTTInstance.this.z + 0.5f, red.getOne(), red.getTwo(), red.getThree(), 1, 0, 0);
                        ((CraftPlayer) two.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }


                    //add half a block to both coords
                    i+=0.5;
                    if(i>TTTInstance.this.x +29){
                        i=TTTInstance.this.x;
                    }
                    j-=0.5;
                    if(j < TTTInstance.this.y - 28){
                        j = TTTInstance.this.y+1;
                    }

                }
            }, 0L, 1L);
            return;
        }
        //the boolean was set to OFF; so cancel the current particles!
        Bukkit.getScheduler().cancelTask(particles);
    }

    /**
     * Sets block X,Y,Z to Material
     * @param x x-location
     * @param y y-location
     * @param z z-location
     * @param mat Material of the block
     */
    private void setBlock(int x, int y, int z, Material mat){
        new Location(Bukkit.getWorlds().get(0), x, y, z).getBlock().setType(mat);
    }

    /**
     * checks if the board is completed
     * @return true if the board is completed, false if not
     */
    public boolean isDone() {
        return getWinner() != 0 || isFull();
    }

    /**
     * Returns true/false depending if the board is filled
     * @return true if the board is filled, false if not fully filled
     */
    public boolean isFull(){
        for(int i = 0; i < 3; i++){
            for(int j=0;j<3;j++){
                if(field[i][j]==0){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Resets this board instance
     */
    public void resetBoard(){
        for(int i=0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                for (int k = 0; k < 4; k++) {
                    setBlock(this.x + i, this.y - j, this.z + k, Material.AIR);
                }
            }
        }
        setParticles(false, null, null, null);
    }

    /**
     * Draws the winner of this instance, or a tie.
     * @param pawn Pawn to be drawn for the winner (if any)
     */
    private void drawWinner(Pawn pawn){
        Material mat = Material.REDSTONE_BLOCK;
        int format =getWinningFormat();
        switch(format) {
            case 0:
                //it was a draw, layer some coal blocks over it to indicate nobody won.
                mat = Material.COAL_BLOCK;
                for (int i = 0; i < 3; i++){
                    for (int y = 0; y < 29; y++) {
                        setBlock((this.x + 4) + (i * 10), this.y - y, this.z, mat);
                    }
                    for(int x = 0; x < 29; x++){
                        setBlock(this.x+x, (this.y-4)-(i*10), this.z, mat);
                    }
                }
                break;

            //make a redstone stripe for all other cases, depending on how the win format was.
            case 1:
            case 2:
            case 3:
                for(int y = 0; y < 29; y++){
                    setBlock((this.x+4)+((format-1)*10), this.y - y, this.z, mat);
                }
                break;
            case 4:
            case 5:
            case 6:
                for(int x = 0; x < 29; x++){
                    setBlock(this.x+x, (this.y-4)-((format-4)*10), this.z, mat);
                }
                break;
            case 7:
                for(int x = 0; x < 29; x++){
                    setBlock(this.x+x, this.y-x, this.z, mat);
                }
                break;
            case 8:
                for(int x = 0; x < 29; x++){
                    setBlock(this.x+28-x, this.y-x, this.z, mat);
                }
                break;
        }
        //draw the big winner too, if it exists. (If it doesnt exists, itll be caught in drawBig)
        drawBig(pawn);
    }

    /**
     * Draws a big pawn for the winning player in this instance
     * @param pawn Pawn to been draw for the winning Player
     */
    private void drawBig(Pawn pawn){
        TTTPlayer p = getWinningPlayer();
        if(p!=null){
            pawn.copy(this.x, this.y, this.z+1, 1, p==pCross ? p.getPrimaryMaterial() : pCircle.getPawnMaterial(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()), p==pCross?p.getPrimaryMaterialData():pCircle.getPawnMaterialData(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()));
        }
    }

    /**
     * Gets the winning Player of this instance
     * @return The winner as TTTPlayer, or null if there was no winner
     */
    private TTTPlayer getWinningPlayer(){
        switch(getWinner()){
            case 1:
                return pCross;
            case 2:
                return pCircle;
            default:
                return null;
        }
    }

    /**
     * Gets the winner as integer format (1 for X, 2 for O, -1 for a draw, 0 for no winner yet)
     * @return 1 for X winner, 2 for O winner, -1 for a draw, 0 for no winner yet (or error)
     */
    public int getWinner(){
        if(this.winner!=0){
            return winner;
        }
        int winner = getWinningFormat();
        switch(winner){
            case 1:
            case 4:
            case 7:
                this.winner = field[0][0];
                break;
            case 5:
                this.winner = field[1][0];
                break;
            case 6:
                this.winner = field[2][0];
                break;
            case 2:
                this.winner = field[0][1];
                break;
            case 8:
            case 3:
                this.winner = field[0][2];
                break;
            case 0:
                this.winner = -1;
                break;
            default:
                this.winner = 0;
        }
        return this.winner;
    }

    /**
     * 1-3 vertical, 4-6 horizontal, 7&8 diagonal
     *  7 1 2 3 8
     * 4 | | | |
     * 5 | | | |
     * 6 | | | |
     * @return the winning format as an integer 0-8
     */
    private int getWinningFormat(){
        if(field[0][0] == field[0][1] && field[0][0] == field[0][2] && field[0][0]!=0){
            return 4;
        }
        else if(field[1][0] == field[1][1] && field[1][0] == field[1][2] && field[1][0]!=0){
            return 5;
        }
        else if(field[2][0] == field[2][1] && field[2][0] == field[2][2] && field[2][0]!=0){
            return 6;
        }
        else if(field[0][0] == field[1][0] && field[0][0] == field[2][0] && field[0][0]!=0){
            return 1;
        }
        else if(field[0][1] == field[1][1] && field[0][1] == field[2][1] && field[0][1]!=0){
            return 2;
        }
        else if(field[0][2] == field[1][2] && field[0][2] == field[2][2] && field[0][2]!=0){
            return 3;
        }
        else if(field[0][0] == field[1][1] && field[0][0] == field[2][2] && field[0][0]!=0){
            return 7;
        }
        else if(field[0][2] == field[1][1] && field[0][2] == field[2][0] && field[0][2]!=0){
            return 8;
        }
        if(isFull()) {
            return 0;
        }
        return -1;
    }

    /**
     * Calculates how many squares were filled in in the board
     * @return Amount of filled squares
     */
    public int getFilledSquares(){
        int filled = 0;
        for(int i = 0; i < 9; i++){
            if(field[i/3][i%3]!=0){
                filled++;
            }
        }
        return filled;
    }
}
