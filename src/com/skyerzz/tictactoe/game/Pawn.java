package com.skyerzz.tictactoe.game;

import com.skyerzz.tictactoe.TTTPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

/**
 * Pawns and their locations
 * Created by sky on 5-9-2017.
 */
public enum Pawn {
    X("X (Default)", 314,113,155, 314,113,157, 0),
    O("O (Default)", 346,113,155, 314,113,158, 0),
    CROSS("Sharp cross", 314, 81, 155, 314, 113, 159, 0),
    PEACE("Peace", 346, 81, 155, 314, 113, 160, 0),
    STRONGMAN("Strongman", 378, 81, 155, 314, 113, 161, 0),
    HEART("Heart", 314, 49, 155, 314, 113, 162, 0);

    /** top-left x,y,z coordinates */
    private int x, y, z;
    /** top-left x,y,z coordinates for the large pawn */
    private int xBig, yBig, zBig;
    /** DisplayName used in Shops */
    private String displayName;
    /** price if the pawn needs to be bought */
    private int price;

    /** sizes of the pawns */
    private final int smallSize = 9, midSize = 29, bigSize = 93;
    /** if the pawn is of type O or X */

    /**
     *
     * @param displayName Name used in shops
     * @param x top-left x
     * @param y top-left y
     * @param z top-left z
     * @param xBig top-left x for the big pawn
     * @param yBig top-left y for the big pawn
     * @param zBig top-left z for the big pawn
     * @param price price of the pawn
     */
    Pawn(String displayName, int x, int y, int z, int xBig, int yBig, int zBig, int price){
        this.x = x;
        this.y = y;
        this.z = z;
        this.xBig = xBig;
        this.yBig = yBig;
        this.zBig = zBig;

        this.price = price;

        this.displayName = displayName;
    }


    /**
     * Copies the pawn of the given type to the given x,y,z location
     * @param x top-left x to be copied to
     * @param y top-left y to be copied to
     * @param z top-left z to be copied to
     * @param type pawn type, 0 = small, 1= medium, 2 = big
     */
    public void copy(int x, int y, int z, int type, Material material, byte data){
        switch(type){
            case 0: //small one
                for(int i = 0; i < smallSize; i++){
                    for(int j = 0; j < smallSize; j++){
                        Location l = new Location(Bukkit.getWorlds().get(0), x+i, y-j, z);
                        Location old = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z);
                        if(old.getBlock().getType()!=Material.AIR) {
                            l.getBlock().setType(material);
                            l.getBlock().setData(data);
                        }
                    }
                }
                fixLighting(x, y, z+1, type);
                return;
            case 1: //medium one
                for(int i = 0; i < midSize; i++){
                    for(int j = 0; j < midSize; j++){
                        Location l = new Location(Bukkit.getWorlds().get(0), x+i, y-j, z);
                        Location old = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z+1);
                        if(old.getBlock().getType()!=Material.AIR) {
                            l.getBlock().setType(material);
                            l.getBlock().setData(data);
                        }
                    }
                }
                fixLighting(x, y, z+1, type);
                return;
            case 2: //large one
                for(int i = 0; i < bigSize; i++){
                    for(int j = 0; j < bigSize; j++){
                        Location l = new Location(Bukkit.getWorlds().get(0), x+i, y-j, z);
                        Location old = new Location(Bukkit.getWorlds().get(0), this.xBig + i, this.yBig - j, this.zBig);
                        if(old.getBlock().getType()!=Material.AIR) {
                            l.getBlock().setType(material);
                            l.getBlock().setData(data);
                        }
                    }
                }
                fixLighting(x, y, z+1, type);
                return;
            case 3: //medium one below names
                for(int i = 0; i < midSize; i++){
                    for(int j = 0; j < midSize; j++){
                        Location old = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z+1);
                        Location l = new Location(Bukkit.getWorlds().get(0), x, y-j, z-i); //rotate it in Z direction instead of X
                        if(old.getBlock().getType()!=Material.AIR) {
                            l.getBlock().setType(material);
                            l.getBlock().setData(data);
                        }
                    }
                }
                //fix twice here cause its used both on the left and right side.
                fixLighting(x+1, y, z, type);
                fixLighting(x-1, y, z, type);
                return;
        }
    }

    /**
     * Fixes the lighting issues by replacing a block with itsself in front of the board. it works, dont touch.
     * @author sky
     * @param x x coord of starting corner
     * @param y y coord of starting corner
     * @param z z coord of starting corner
     * @param type type of the board (3 for sideboards, anything else for main)
     */
    private void fixLighting(int x, int y, int z, int type){
        //theres a logic flaw in here somewhere, causing lighting to not always update on further boards. XYZ is relative instead of static probably. Not a big issue at this moment.
        if(type==3){
            for(int i = 0; i < midSize; i++){
                for(int j = 0; j < 80; j++){
                    Location loc = new Location(Bukkit.getWorlds().get(0), x, y-j, z-i);
                    byte data = loc.getBlock().getData();
                    loc.getBlock().setType(loc.getBlock().getType());
                    loc.getBlock().setData(data);
                }
            }
            return;
        }
        for(int i = 0; i < bigSize; i++){
            for(int j = 0; j < bigSize; j++){
                Location loc = new Location(Bukkit.getWorlds().get(0), x+i, y-j, z+1);
                byte data = loc.getBlock().getData();
                loc.getBlock().setType(loc.getBlock().getType());
                loc.getBlock().setData(data);
            }
        }
    }

    /**
     * Shows the particles to the given player, in the given color, for the current pawn
     * @param x x-location
     * @param y y-location
     * @param z z-location
     * @param color ParticleColor of the particles
     * @param player Player who needs to see the particles
     */
    public void showParticles(int x, int y, int z, ParticleColor color, TTTPlayer player){
        //loop over the entire pawn (small size)
        for(int i = 0; i < smallSize; i++){
            for(int j = 0; j < smallSize; j++){
                Location l = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z);
                if(l.getBlock().getType() != Material.AIR){
                    //if the pawn has a block thats not air (e.g. a real block) , show particles on that block
                    showParticleBlock(x+i, y-j, z, color, player);
                }
            }
        }
        return;
    }

    /**
     * Shows Colored Redstone particles on block X,Y,Z for the player
     * @param x x-location
     * @param y y-location
     * @param z z-location
     * @param color ParticleColor of the particles
     * @param player Player who needs to see the particles
     */
    private void showParticleBlock(int x, int y, int z, ParticleColor color, TTTPlayer player){
        //move it around slightly so its a nice block that gets covered. ~9 particles for 1 block
        for (float q = 0.05f; q < 1.0f; q += 0.3f) {
            for (float r = 0.05f; r < 1.0f; r += 0.3f) {
                //send a packet to the player to let them show the particles
                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) x+q, (float) y+r, (float) z, color.getOne(), color.getTwo(), color.getThree(), 1, 0, 0);
                ((CraftPlayer) player.getPlayer()).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    /**
     * Loops over the pawn to find the first block, and returns the material of that
     * @return Material of the pawn, or Coal if no material is found
     */
    public Material getMaterial(){
        //loop over the entire pawn
        for(int i = 0; i < smallSize; i++){
            for(int j = 0; j < smallSize; j++){
                Location old = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z);
                if(old.getBlock().getType()!=Material.AIR){
                    //if the blocks is from the pawn (not air), return the type of the block
                    return old.getBlock().getType();
                }
            }
        }
        //if it wasnt found (error?), return coal instead
        return Material.COAL;
    }

    /**
     * Loops over the pawn to find the first block, and returns the data of that. (Needed for Colored wood/ rotated stairs, etc)
     * @return The Data of the bpawn
     */
    public byte getMaterialData(){
        //loop over the entire pawn
        for(int i = 0; i < smallSize; i++){
            for(int j = 0; j < smallSize; j++){
                Location old = new Location(Bukkit.getWorlds().get(0), this.x + i, this.y - j, this.z);
                if(old.getBlock().getType()!=Material.AIR){
                    //if the block is from the pawn (not air), return the data of the block
                    return old.getBlock().getData();
                }
            }
        }
        //if it wasnt found (error), return -1 instead
        return -1;
    }

    /**
     * Gets the displayname of the pawn
     * @return The name of the pawn
     */
    public String getDisplayName(){
        return displayName;
    }

    /**
     * Gets the price of the pawn
     * @return Price of the pawn
     */
    public int getPrice(){
        return price;
    }

    /**
     * Formats the price into a neat string with ',' each 3 numbers
     * @return Formatted Price of the pawn
     */
    public String getFormattedPrice(){
        String priceString = "";
        int p = price;
        int i = 0;
        //adds a ',' every 3 numbers, starting from the right side
        while(p>0) {
            int off = p % 10;
            priceString = off + priceString;
            p/=10;
            if(p>0 && (++i)%3==0){
                priceString = "," + priceString;
            }
        }
        return priceString;
    }

    /**
     * Finds the pawn associated with the name
     * @param name Unformatted DisplayName of the pawn
     * @return Pawn associated with the displayname
     */
    public static Pawn getPawnByDisplayName(String name){
        for(Pawn p: Pawn.values()){
            if(p.getDisplayName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }

    /**
     * Enum for the type of pawn
     * NOT USED IS LIVE VERSION.
     */
    @Deprecated
    public enum PawnType{
        X,
        O
    }
}
