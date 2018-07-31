package com.skyerzz.tictactoe.game.names;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Location of letters that can be copied and pasted in-game
 * Created by sky on 6-10-2017.
 */
public enum Letter {
    A('A', 128, 212, 4),
    B('B', 128, 217, 4),
    C('C', 128, 222, 4),
    D('D', 128, 227, 4),
    E('E', 128, 232, 4),
    F('F', 128, 237, 4),
    G('G', 128, 242, 4),
    H('H', 120, 212, 4),
    I('I', 120, 217, 3),
    J('J', 120, 221, 4),
    K('K', 120, 226, 4),
    L('L', 120, 231, 4),
    M('M', 120, 236, 4),
    N('N', 120, 241, 4),
    O('O', 112, 212, 4),
    P('P', 112, 217, 4),
    Q('Q', 112, 222, 4),
    R('R', 112, 227, 4),
    S('S', 112, 232, 4),
    T('T', 112, 237, 3),
    U('U', 112, 241, 4),
    V('V', 104, 212, 4),
    W('W', 104, 217, 4),
    X('X', 104, 222, 4),
    Y('Y', 104, 227, 4),
    Z('Z', 104, 232, 4),
    ONE('1', 104, 237, 3),
    TWO('2', 104, 241, 4),
    THREE('3', 96, 212, 4),
    FOUR('4', 96, 217, 4),
    FIVE('5', 96, 222, 4),
    SIX('6', 96, 227, 4),
    SEVEN('7', 96, 232, 4),
    EIGHT('8', 96, 237, 4),
    NINE('9', 96, 242, 4),
    ZERO('0', 88, 212, 4),
    UNDERSCORE('_', 88, 217, 4);

    /** char value of the letter */
    private char letter;
    /** top x,y,z values & the width/height for a char */
    private int x=290,y,z,width,height=7;

    /**
     *
     * @param letter the char value for the current letter/digit
     * @param y the top t coordinate
     * @param z the top-left z coordinate
     * @param width the width of the letter in blocks
     */
    Letter(char letter, int y, int z, int width){
        this.letter = letter;
        this.y = y;
        this.z = z;
        this.width = width;
    }

    /**
     *
     * @param x the top-left X coordinate to be copied to
     * @param y the top-left Y coordinate to be copied to
     * @param z the top-left Z coordinate to be copied to
     * @param leftSide boolean value if its on the leftside. true if left-side, false if right-side.
     */
    public void CopyTo(int x, int y, int z, boolean leftSide){
        int newX = leftSide ? this.x-1 : this.x; //if its on the right-side, adjust the X coordinate to copy the mirrored letters instead

        //loop over the template letter Height and Width, so we copy everything
        for(int i = 0; i < this.height; i++){
            for(int j=0; j < this.width; j++){
                Location old =  new Location(Bukkit.getWorlds().get(0), newX, this.y-i, this.z+j);
                Location newLoc = new Location(Bukkit.getWorlds().get(0), x, y-i, z-this.width+j);
                //set the new block type & data.
                newLoc.getBlock().setType(old.getBlock().getType());
                newLoc.getBlock().setData(old.getBlock().getData());
            }
        }
    }

    /**
     *
     * @return the symbol as char
     */
    public char getChar(){
        return this.letter;
    }

    /**
     *
     * @return the width of the symbol
     */
    public int getWidth(){
        return this.width;
    }

    /**
     *
     * @param c symbol as type Character
     * @return the instance of the symbol that represents the character
     */
    public static Letter getLetterFromChar(char c){
        for(Letter letter:Letter.values()){
            if(letter.getChar() == c){
                return letter;
            }
        }
        return null;
    }

    /**
     *
     * @param s String to be calculated
     * @return the total width of the string in blocks, including spaces
     */
    public static int getTotalWidth(String s){
        s = s.toUpperCase();
        int width = -1; //init at -1 , so we can easily add spaces after the letter count, without counting one too much.
        for(char c: s.toCharArray()){
            width += Letter.getLetterFromChar(c).getWidth()+1;
        }
        return width;
    }
}
