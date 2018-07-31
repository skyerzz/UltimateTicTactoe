package com.skyerzz.tictactoe.game.names;

import com.skyerzz.tictactoe.game.TTTBoard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Class to build someone's username in-game
 * Created by sky on 6-10-2017.
 */
public class NameBuilder {

    private String name;
    private boolean leftSide;
    private int arena = 0;
    private int x,y=112,z=260;
    private int width = 51;

    /**
     *
     * @param name The name to build as string
     * @param leftSide boolean value if its on the left side or not
     * @param arena interger value for the arena number the name should be built in
     */
    public NameBuilder(String name, boolean leftSide, int arena){
        this.name = name;
        this.leftSide = leftSide;
        this.arena = arena;
        this.x = (leftSide) ? 292+(TTTBoard.ArenaXWidth*arena) : 406+(TTTBoard.ArenaXWidth*arena);
    }

    /**
     * Builds the name into the arena
     */
    public void build(){
        String name = getMaxName();
        int width = Letter.getTotalWidth(name);
        int startZ = this.z - this.width/2 + width/2;

        name = leftSide ? name : reverseName(name);

        for(char c:name.toUpperCase().toCharArray()){
            Letter l = Letter.getLetterFromChar(c);
            l.CopyTo(this.x, this.y, startZ, this.leftSide);
            startZ-=(l.getWidth()+1);
        }
    }

    /**
     *
     * @return the name, truncated to the maximum space available in the arena
     */
    private String getMaxName(){
        String s = name;
        while(Letter.getTotalWidth(s)>51){
            s = s.substring(0, s.length()-1);
        }
        return s;
    }

    /**
     *
     * @param name The name to be reversed
     * @return the reversed value of the string
     */
    private String reverseName(String name){
        String reverse = "";
        for(char c:name.toCharArray()){
            reverse = String.valueOf(c)+reverse;
        }
        return reverse;
    }

    /**
     * Resets the name-space to air blocks
     */
    public void reset(){
        for(int i = y-8; i <= y; i++){
            for(int j =z; j >= z-width; j--){
                Location block =  new Location(Bukkit.getWorlds().get(0), this.x, i, j);
                block.getBlock().setType(Material.AIR);
            }
        }
    }
}
