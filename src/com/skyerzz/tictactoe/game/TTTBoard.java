package com.skyerzz.tictactoe.game;

import com.skyerzz.tictactoe.TTTPlayer;
import com.skyerzz.tictactoe.TicTacToe;
import com.skyerzz.tictactoe.TicTacToeCommand;
import com.skyerzz.tictactoe.game.names.NameBuilder;
import com.skyerzz.tictactoe.game.shop.GeneralShopItem;
import com.skyerzz.tictactoe.game.sound.SoundManager;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Entire board (playing field), main source of the game.
 * Created by sky on 7-8-2017.
 */
public class TTTBoard implements Listener{

    private final int space = ArenaXWidth; //space between games
    private final int initX = 303;
    private final int topleftY = 127, topleftZ = 208;
    private final int boardspace = 32;

    public static int ArenaTopX = 291, ArenaTopY = 130, ArenaTopZ = 206, ArenaXWidth = 122;

    private TTTPlayer pCross;
    private TTTPlayer pCircle;
    private ArrayList<Player> spectators = new ArrayList<>();
    private HashSet<Material> mats = new HashSet<>();

    /** true if its the move of pCross, false if its pCircle */
    private boolean moveX;

    private TTTInstance[][] board;
    private int gameNumber;
    private int particles = -1;

    private int lookRun = 0;
    private int activeBoard = -1;
    private int winner = 0;//0=nobody,1=x,2=o

    private final long startTime = System.currentTimeMillis();

    /**
     * Used only to reset an arena without needing players
     * @param gameNumber arenaNumber of this board
     */
    public TTTBoard(int gameNumber){
        this.gameNumber = gameNumber;
        instanceBoards();
        Bukkit.getServer().getPluginManager().registerEvents(this, TicTacToe.pluginInstance);

    }

    /**
     * Instances a new Arena for a battle between the two given players.
     * @param cross CrossPlayer
     * @param circle CirclePlayer
     * @param gameNumber ArenaNumber of the match
     */
    public TTTBoard(TTTPlayer cross, TTTPlayer circle, int gameNumber){
        this.pCross = cross;
        this.pCircle = circle;
        this.gameNumber = gameNumber;

        //find out who gets the first move
        selectFirstMove();
        //make sure that the boards are indexed
        instanceBoards();
        //make sure the players are ok. We dont want to hurt them
        instancePlayers();

        //legacy code, not sure if still needed. Afraid to remove it currently. //todo
        mats.add(Material.WOOL); //O
        mats.add(Material.WOOD); //X
        mats.add(Material.AIR); //air
        mats.add(Material.REDSTONE_BLOCK); //stripes
        mats.add(Material.COAL_BLOCK); //draw stripes

        //register the events from this thing
        Bukkit.getServer().getPluginManager().registerEvents(this, TicTacToe.pluginInstance);

        //schedule a new repeating task
        onPlayerLook();
    }

    /**
     * Select the first move, and let the players know.
     */
    private void selectFirstMove(){
        this.moveX = System.currentTimeMillis()%2 == 1; //generates a random boolean value

        //let the players know who's move it is
        if(moveX){
            pCross.getPlayer().sendMessage("[TicTacToe] You have the first move!");
            pCircle.getPlayer().sendMessage("[TicTacToe] " +  pCross.getPlayer().getName() + " Starts the game!");
        }else{
            pCircle.getPlayer().sendMessage("[TicTacToe] You have the first move!");
            pCross.getPlayer().sendMessage("[TicTacToe] " +  pCircle.getPlayer().getName() + " Starts the game!");
        }
    }

    /**
     * Creates a new TTTInstance for every 3*3board
     */
    private void instanceBoards(){
        board = new TTTInstance[3][3];
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                board[i][j] = new TTTInstance(initX+(space*gameNumber) + (boardspace*i), topleftY-(boardspace*j), topleftZ, pCross, pCircle);
            }
        }
    }

    /**
     * Do stuff so the players can actually play
     */
    private void instancePlayers(){

        //set names in arena
        new NameBuilder(pCross.getPlayer().getName(), true, this.gameNumber).build();
        new NameBuilder(pCircle.getPlayer().getName(), false, this.gameNumber).build();

        //set particles for current player (sideboard)
        setActivePlayerParticles(moveX);

        //set pawns below names
        pCross.getPrimaryPawn().copy(292 + (space*gameNumber), 103, 249, 3, pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData());
        pCircle.getPawn(pCross.getPrimaryPawn()).copy(406 + (space*gameNumber), 103, 249, 3, pCircle.getPawnMaterial(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()), pCircle.getPawnMaterialData(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()));

        //teleportPlayers
        Location crossLocation = new Location(pCross.getPlayer().getWorld(), 344 + (space*gameNumber), 83.1, 279.5, 180f, 0f);
        Location circleLocation = new Location(pCircle.getPlayer().getWorld(), 354 + (space*gameNumber), 83.1, 279.5, 180f, 0f);

        pCross.getPlayer().teleport(crossLocation);
        pCircle.getPlayer().teleport(circleLocation);

        //allow them to fly and stuffs
        pCircle.getPlayer().setAllowFlight(true);
        pCircle.getPlayer().setFlying(true);

        pCross.getPlayer().setAllowFlight(true);
        pCross.getPlayer().setFlying(true);


        pCross.getPlayer().getInventory().setItem(8, GeneralShopItem.getHowToPlayBook());
        pCircle.getPlayer().getInventory().setItem(8, GeneralShopItem.getHowToPlayBook());


        //give the person who currently has the turn the items to indicate it
        swapMoveItems();
    }

    /**
     * Removes a spectator from the game
     * @param player Spectator to remove
     * @return true if succesful, false if unsuccesful (e.g. the spectator didnt exist as spectator)
     */
    public boolean removeSpectator(Player player){
        if(!spectators.contains(player)){
            // we didnt have a spectator by this player?
            return false;
        }
        //rip spectator
        spectators.remove(player);
        //dont forget to set them back to no-fly mode! (only if they arent in Creative mode, otherwise they cant fly in creative...)
        if(player.getGameMode()!= GameMode.CREATIVE) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        //teleport the player back to spawn
        player.teleport(new Location(player.getWorld(), 0.5, 17.1, 0.5));
        player.getInventory().clear();
        return true;
    }

    /**
     * Adds a spectator to the game
     * @param player Player to be added as a spectator
     */
    public void addSpectator(Player player){
        if(spectators.contains(player)){
            return; //dont add him again, spectating once is enough.
        }
        //WE GOT OURSELFS A SPECTATOR BOYS!
        //lets teleport him to the arena
        player.teleport(new Location(player.getWorld(), 349 + (space * gameNumber), 83.1, 286.5, 180f, 0f));
        //add him AFTER teleportation, as we have a teleportevent below that triggers otherwise.
        spectators.add(player);
        //lets also allow him to fly
        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().setItem(7, GeneralShopItem.getHowToPlayBook());
        player.getInventory().setItem(8, getSpecItem());
    }

    /**
     * kicks out spectators if they teleported.
     * @param e PlayerTeleportEvent
     */
    @EventHandler
    private void onPlayerTeleport(PlayerTeleportEvent e){
        if(spectators.contains(e.getPlayer())){
            e.getPlayer().sendMessage("[TTT] Returning you to lobby due to a teleport...");
            removeSpectator(e.getPlayer());
        }
    }

    /**
     * Creates a repeating runnable task that shows particles to certain players if it's their move.
     */
    private void onPlayerLook(){
        //get the integer for this task in a global variable, so we can cancel it later
        lookRun = Bukkit.getScheduler().scheduleSyncRepeatingTask(TicTacToe.pluginInstance, ()-> {

                //get the player who's move it currently is
                TTTPlayer player = (moveX) ? pCross : pCircle;
                //get what block they are looking at
                Block b = player.getPlayer().getTargetBlock(mats, 100);
                if(b.getZ()!= topleftZ -1 && b.getZ()!= topleftZ && b.getZ()!= topleftZ+1){
                    //not on our board! nothing to do further
                    return;
                }

                //OMG ITS OUR BOARD YAY
                int board = getBoardNumber(b.getX(), b.getY());
                if(board==-1){
                    //it was somewhat our board.. Just, not on an actual board, but the pillars in between
                    return;
                }
                //get the place of X and Y on the board
                int x = getPlace((b.getX()-(initX+(space*gameNumber))+1)%boardspace);
                int y = getPlace((b.getY()-34)%boardspace);
                if(x==-1 || y==-1){
                    //RIP particles :(
                    return;
                }
                //fix for y cause its mirrored
                y = (y==2) ? 0 : y==1 ? 1 : 2;

                if((activeBoard!=board && activeBoard!=-1) || getBoard(board).isDone()){
                    //the board they are looking at isnt one they can place it on, or the board is already finished (meaning the same)
                    return;
                }
                //IT ALL WORKED! Lets give them some nice particles to indicate they can put their move there!
                getBoard(board).squareParticles(y, x, player);
            }
        , 1L, 0L);
    }

    /**
     * Prevents our players & spectators from (re)moving items from their inventory.
     * @param e InventoryInteractEvent
     */
    @EventHandler
    private void onPlayerItemMove(InventoryInteractEvent e){
        if(!(e.getInventory().getHolder() instanceof Player)){
            //its not a players own inventory.
            return;
        }
        Player p = (Player) e.getInventory().getHolder();
        if(p.getUniqueId()==pCircle.getPlayer().getUniqueId() || p.getUniqueId() == pCross.getPlayer().getUniqueId()){
            e.setCancelled(true);
            return;
        }
        for(Player spec:spectators){
            if(p.getUniqueId()==spec.getUniqueId()){
                e.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Registers a click from the player, and does something based on that
     * @param e PlayerInteractEvent (left/rightclick)
     */
    @EventHandler
    private void onPlayerClick(PlayerInteractEvent e){
        if((e.getAction()==Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR) && spectators.contains(e.getPlayer())){
            //the user left or right clicked, and is a spectator of ours!
            if(e.getItem().getType() == Material.ARROW){
                //they clicked the go away button, lets send them back to a lobby.
                e.getPlayer().sendMessage("[TTT] Sending you back to the lobby...");
                removeSpectator(e.getPlayer());
                return;
            }
        }
        if(e.getPlayer()!= pCross.getPlayer() && e.getPlayer()!=pCircle.getPlayer()){
            //pff how lame, its not even our player
            return;
        }
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR){
            Block b = e.getPlayer().getTargetBlock(mats, 100);
            if(b.getZ()!= topleftZ -1 && b.getZ()!= topleftZ && b.getZ()!= topleftZ+1){
                //not on our board!
                return;
            }

            //OMG ITS OUR BOARD YAY
            int board = getBoardNumber(b.getX(), b.getY());


            int x = getPlace((b.getX()-(initX+(space*gameNumber))+1)%boardspace);
            int y = getPlace((b.getY()-34)%boardspace);
            //fix for y cause its mirrored
            y = (y==2) ? 0 : y==1 ? 1 : (y==0) ? 2 : -1;

            //check if a player can do a move there (and if yes, do the move instantly too)
            if(!doPlayerMove(e.getPlayer(), board, y, x)) {
                //the player couldnt put a move there. Lets see why
                if((moveX&&e.getPlayer()==pCross.getPlayer()) || (!moveX&&e.getPlayer()==pCircle.getPlayer())){
                    //its not the players turn!
                    e.getPlayer().sendMessage("[TicTacToe] You cannot place your move there!");
                }else{
                    //i dont know where the clicked, but they cant do it!
                    e.getPlayer().sendMessage("[TicTacToe] It's not your turn!");
                }
                return;
            }
            //change the move around :)
            moveX = !moveX;
            //reset all particles for boards
            //i did this before for getBoard(activeBoard) but that didnt always work for some reason. so just resetting all now
            for(int i = 0; i < 9; i++){
                getBoard(i).setParticles(false, null, null, spectators);
            }

            //set the new active board
            this.activeBoard = (y*3)+x;

            //check if the game is done
            if(isDone()) {
                //its done? ITS DONE! Lets announce our new winner!
                win(getWinner()==1 ? pCross : getWinner()==2 ? pCircle : null);
                return;
            }

            //set the particles for the new situation
            setActiveBoardParticles();
            setActivePlayerParticles(moveX);

            //lets give the other player some items to indicate its his move
            swapMoveItems();


        }
    }

    /**
     * Gives the current mover items, removes items from the previous mover
     */
    private void swapMoveItems(){
        Player give = moveX ? pCross.getPlayer() : pCircle.getPlayer();
        Player take = !moveX ? pCross.getPlayer() : pCircle.getPlayer();

        //set the items on the entire hotbar EXCEPT slot 8, as that contains the how to play book
        for(int i = 0; i < 8 ; i++) {
            give.getInventory().setItem(i, getTurnItem());
            take.getInventory().setItem(i, null);
        }
        //lets also play them a sound effect!
        SoundManager.playMoveSound(take);
        SoundManager.playMoveDoneSound(give);

    }

    /**
     * Creates an item to indicate its their turn
     * @return ItemStack of 'your turn' items
     */
    private ItemStack getTurnItem(){
        ItemStack item =  new ItemStack(Material.STICK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A7o\u00A75Your turn!");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Why are you looking here?");
        lore.add("It's your move!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Creates an item to indicate its their turn
     * @return ItemStack of 'your turn' items
     */
    private ItemStack getSpecItem(){
        ItemStack item =  new ItemStack(Material.ARROW, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("\u00A7o\u00A75click to return!");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Right or left click to return to the lobby!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Checks & Returns if the game is completed or not
     * @return true if the game is completed, false if not completed
     */
    public boolean isDone() {
        return getWinner() != 0 || isFull();
    }

    /**
     * Checks if all boards are done
     * @return true if the boards are all full (or completed), false if this is not the case
     */
    public boolean isFull(){
        for(int i = 0; i < 9; i++){
            if(!getBoard(i).isDone()){
                return false;
            }
        }
        return true;
    }


    /**
     * Finds the winner of the game, if it exists
     * @return 1 for X winner, 2 for O winner, -1 for a Draw, 0 for no winner yet (or error)
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
                this.winner = getBoard(0).getWinner();
                break;
            case 5:
                this.winner = getBoard(3).getWinner();
                break;
            case 6:
                this.winner = getBoard(6).getWinner();
                break;
            case 2:
                this.winner = getBoard(1).getWinner();
                break;
            case 8:
            case 3:
                this.winner = getBoard(2).getWinner();
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
     * Gets an integer code based on what the pattern of the win was
     * 1-3 vertical, 4-6 horizontal, 7&8 diagonal
     *  7 1 2 3 8
     * 4 | | | |
     * 5 | | | |
     * 6 | | | |
     * @return integer from 0 - 8 based on the win sequence
     */
    private int getWinningFormat(){
        if(getBoard(0).getWinner() > 0 && getBoard(0).getWinner() == getBoard(1).getWinner() && getBoard(0).getWinner() == getBoard(2).getWinner()){
            return 4;
        }
        if(getBoard(3).getWinner() > 0 && getBoard(3).getWinner() == getBoard(4).getWinner() && getBoard(3).getWinner() == getBoard(5).getWinner()){
            return 5;
        }
        if(getBoard(6).getWinner() > 0 && getBoard(6).getWinner() == getBoard(7).getWinner() && getBoard(6).getWinner() == getBoard(8).getWinner()){
            return 6;
        }
        if(getBoard(0).getWinner() > 0 && getBoard(0).getWinner() == getBoard(3).getWinner() && getBoard(0).getWinner() == getBoard(6).getWinner()){
            return 1;
        }
        if(getBoard(1).getWinner() > 0 && getBoard(1).getWinner() == getBoard(4).getWinner() && getBoard(1).getWinner() == getBoard(7).getWinner()){
            return 2;
        }
        if(getBoard(2).getWinner() > 0 && getBoard(2).getWinner() == getBoard(5).getWinner() && getBoard(2).getWinner() == getBoard(8).getWinner()){
            return 3;
        }
        if(getBoard(0).getWinner() > 0 && getBoard(0).getWinner() == getBoard(4).getWinner() && getBoard(0).getWinner() == getBoard(8).getWinner()){
            return 7;
        }
        if(getBoard(2).getWinner() > 0 && getBoard(2).getWinner() == getBoard(4).getWinner() && getBoard(2).getWinner() == getBoard(6).getWinner()){
            return 8;
        }
        if(isFull()) {
            return 0;
        }
        return -1;
    }

    /**
     * Sets the block at x,y,z to Material
     * @param x x-location
     * @param y y-location
     * @param z z-location
     * @param mat Material
     */
    private void setBlock(int x, int y, int z, Material mat){
        new Location(Bukkit.getWorlds().get(0), x, y, z).getBlock().setType(mat);
    }

    /**
     * Does stuff based on who won the game
     * @param p TTTPlayer who won the game, or null if nobody won
     */
    private void win(TTTPlayer p){
        //send a message based on who won
        //if a player won, draw their icon in a giant format
        if(p==null){
            pCircle.getPlayer().sendMessage("[TTT] It's a draw!");
            pCross.getPlayer().sendMessage("[TTT] It's a draw!");
        }else if(p.getPlayer().getUniqueId() == pCircle.getPlayer().getUniqueId()){
            pCircle.getPlayer().sendMessage("[TTT] You Won!");
            pCross.getPlayer().sendMessage("[TTT] You lost!");
            drawBig(pCircle);
        }else{
            pCross.getPlayer().sendMessage("[TTT] You Won!");
            pCircle.getPlayer().sendMessage("[TTT] You lost!");
            drawBig(pCross);
        }

        //add stats to the players
        pCircle.addGameData(new GameData(this));
        pCross.addGameData(new GameData(this));

        //teleport all players back to the lobby after 160 ticks
        Bukkit.getScheduler().runTaskLater(TicTacToe.pluginInstance, () -> {

            //remove all spectators first
            ArrayList<Player> specs = (ArrayList<Player>) spectators.clone();
            specs.forEach(this::removeSpectator);

            //remove our 2 players, and clear their inventories
            pCross.getPlayer().teleport(new Location(pCross.getPlayer().getWorld(), 0.5, 17.1, 0.5));
            if(pCross.getPlayer().getGameMode()!= GameMode.CREATIVE) {
                pCross.getPlayer().setAllowFlight(false);
                pCross.getPlayer().setFlying(false);
            }
            pCross.getPlayer().getInventory().clear();
            pCircle.getPlayer().teleport(new Location(pCircle.getPlayer().getWorld(), 0.5, 17.1, 0.5));
            if(pCircle.getPlayer().getGameMode()!= GameMode.CREATIVE) {
                pCircle.getPlayer().setAllowFlight(false);
                pCircle.getPlayer().setFlying(false);
            }
            pCircle.getPlayer().getInventory().clear();

            //lastly, reset the entire arena
            reset();
        }, 160L);
    }

    /**
     * Draws a massive player Pawn (when a player won)
     * @param player TTTPlayer who's pawn gets drawn
     */
    private void drawBig(TTTPlayer player){
        player.getPawn(player==pCircle ? pCross.getPrimaryPawn() : null).copy(this.initX + (space*gameNumber), this.topleftY, this.topleftZ+2, 2,
                player==pCircle? player.getPawnMaterial(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()) : player.getPrimaryMaterial(),
                player==pCircle? player.getPawnMaterialData(pCross.getPrimaryMaterial(), pCross.getPrimaryMaterialData()) : player.getPrimaryMaterialData());
    }

    /**
     * Triggers when a player logged out
     * @param e playerQuitEvent
     */
    @EventHandler
    private void playerLogout(PlayerQuitEvent e){
        //if one of our players left; let the other player win
        if(e.getPlayer().getUniqueId() == pCircle.getPlayer().getUniqueId()){
            win(pCross);
        }else if(e.getPlayer().getUniqueId() == pCross.getPlayer().getUniqueId()){
            win(pCircle);
        }
        //if it was a spectator instead, remove him from the game
        if(spectators.contains(e.getPlayer())){
            removeSpectator(e.getPlayer());
        }
        //was not our player who quit; not doing anything here.
    }

    /**
     * Triggers when a player drops an item
     * @param e PlayerDropItemEvent
     */
    @EventHandler
    private void onPlayerItemDrop(PlayerDropItemEvent e){
        if(e.getPlayer().getUniqueId() == pCircle.getPlayer().getUniqueId() || e.getPlayer().getUniqueId() == pCross.getPlayer().getUniqueId()){
            e.setCancelled(true);
            return;
        }
        for(Player p: spectators){
            if(p.getUniqueId()==e.getPlayer().getUniqueId()){
                e.setCancelled(true);
                return;
            }
        }
    }

    /**
     * Triggers when a player logged out (but for a kick)
     * @param e PlayerKickEvent
     */
    @EventHandler
    private void playerLogout(PlayerKickEvent e){
        //if one of our players left; let the other player win
        if(e.getPlayer().getUniqueId() == pCircle.getPlayer().getUniqueId()){
            win(pCross);
        }else if(e.getPlayer().getUniqueId() == pCross.getPlayer().getUniqueId()){
            win(pCircle);
        }
        //if it was a spectator instead, remove him from the game
        if(spectators.contains(e.getPlayer())){
            removeSpectator(e.getPlayer());
        }
        //was not our player who got kicked; not doing anything here.
    }

    /**
     * Sets the active board particles for players
     */
    private void setActiveBoardParticles(){
        if(activeBoard==-1){
            //there isnt an active board, no need to set anything
            return;
        }
        TTTInstance board = getBoard(activeBoard);
        if(board.isDone()){
            //the board is already completed, so we can set the active board to -1 , and do nothing
            activeBoard=-1;
            return;
        }

        //lets give them players some particles! Players love particles.
        board.setParticles(true, moveX ? getCrossPlayer() : getCirclePlayer(), moveX ? getCirclePlayer() : getCrossPlayer(), spectators);
    }

    /**
     * Sets the particles on the left (or right) side of the arena, indicating who's turn it is
     * @param leftSide Boolean to indicate leftside (true) or rightside (false)
     */
    private void setActivePlayerParticles(boolean leftSide){
        //cancel the previous particle task if there was one
        if(particles!=-1) {
            Bukkit.getScheduler().cancelTask(particles);
        }

        //set a new particle task which runs every tick
        particles = Bukkit.getScheduler().scheduleSyncRepeatingTask(TicTacToe.pluginInstance, new Runnable() {
            //calculate the coords
            float x = leftSide ? 292f + (space*gameNumber) : 406f + (space*gameNumber);
            float y = 104f;
            float z = 249.75f;
            //count counts when it reaches the end
            float count = 0;

            @Override
            public void run() {
                //jitter the particles a bit
                for(float q = -0.5f; q < 0.5f; q+=0.1f) {
                    //create a packet for each side of the pawn (top,left,right,bottom) and send both players and all spectators that packet

                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y+q, z-count, -1f, 1, 0, 1, 0, 0);
                    ((CraftPlayer) pCross.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) pCircle.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    for(Player spec:spectators) {
                        ((CraftPlayer) spec.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }

                    packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y-29+count, z+q, -1f, 1, 0, 1, 0, 0);
                    ((CraftPlayer) pCross.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) pCircle.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    for(Player spec:spectators) {
                        ((CraftPlayer) spec.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }

                    packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y-count, z-29+q, -1f, 1, 0, 1, 0, 0);
                    ((CraftPlayer) pCross.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) pCircle.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    for(Player spec:spectators) {
                        ((CraftPlayer) spec.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }

                    packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, x, y-29+q, z-29+count, -1f, 1, 0, 1, 0, 0);
                    ((CraftPlayer) pCross.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    ((CraftPlayer) pCircle.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    for(Player spec:spectators) {
                        ((CraftPlayer) spec.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }
                }
                //increase the count to indicate we went a bit further (half a block)
                count = (count+0.5f)%28;
            }
        }, 0L, 1L);
    }

    /**
     * Gets the Coordinate of the filled in integer
     * @param coord Integer value of the coordinate
     * @return Position of the board its on , -1 if no position is found
     */
    private int getPlace(int coord){
        if(coord%10==0){
            return -1;
        }
        return Math.min(coord/10, 2);
    }

    /**
     * Interrupts the game, sending everyone away!
     */
    public void interrupt(){
        //remove both players and all spectators
        if(pCircle!=null && pCircle.getPlayer()!=null) {
            pCircle.getPlayer().sendMessage("[TTT] Your game got interuppted by a developer! Sending you back to the lobby...");
            pCircle.getPlayer().teleport(new Location(pCircle.getPlayer().getWorld(), 0.5, 17.1, 0.5));
            if(pCircle.getPlayer().getGameMode()!= GameMode.CREATIVE) {
                pCircle.getPlayer().setAllowFlight(false);
                pCircle.getPlayer().setFlying(false);
            }
            pCircle.getPlayer().getInventory().clear();
        }
        if(pCross!=null && pCross.getPlayer()!=null) {
            pCross.getPlayer().sendMessage("[TTT] Your game got interuppted by a developer! Sending you back to the lobby...");
            pCross.getPlayer().teleport(new Location(pCross.getPlayer().getWorld(), 0.5, 17.1, 0.5));
            if(pCross.getPlayer().getGameMode()!= GameMode.CREATIVE) {
                pCross.getPlayer().setAllowFlight(false);
                pCross.getPlayer().setFlying(false);
            }
            pCross.getPlayer().getInventory().clear();
        }

        ArrayList<Player> specs = (ArrayList<Player>) spectators.clone();
        specs.forEach(this::removeSpectator);

        reset();
    }

    /**
     * @param x x Coordinate
     * @param y y Coordinate
     * @return board number as integer between 0 & 8 */
    private int getBoardNumber(int x, int y){
        int board = 0;
        if(x< (initX+(space*gameNumber)) || x> initX+(space*gameNumber)+3*boardspace || y > topleftY || y < topleftY-(3*boardspace)){
            //outside of the playing field
            return -1;
        }
        if(x==333+(space*gameNumber) || x==365+(space*gameNumber) || y==65 || y==97){
            //1-block areas between the boards
            return -1;
        }
        if(x > 333+(space*gameNumber)){
            //column 1
            board++;
        }
        if(x > 365+(space*gameNumber)){
            //column 2
            board++;
        }
        if(y < 97){
            //row 2
            board+=3;
        }
        if(y<65 ){
            //row 1
            board+=3;
        }
        return board;
    }

    /**
     * Gets the TTTInstance (3*3 board) from the integer value of a board
     * @param board integer value of the board
     * @return TTTInstance of the board
     */
    private TTTInstance getBoard(int board){
        if(board < 0 || board > 8){
            //someone selected a place outside of the field. Should never happen, cant hurt to check.
            return null;
        }
        return this.board[board%3][board/3];
    }

    /**
     * Executes a player's move if possible, otherwise returns false
     * @param p Player who moves
     * @param board Board on which a move is done
     * @param row The row where a move is being placed
     * @param column The column where a move is being placed
     * @return true if processed correctly, false if the position already had a mark on it/board was deactive
     */
    private boolean doPlayerMove(Player p, int board, int row, int column){
        if(board < 0 || board > 8){
            //someone selected a place outside of the field. Should never happen, cant hurt to check.
            return false;
        }
        if(row==-1 || column==-1){
            //they selected a bar on the board, exactely between squares.
            return false;
        }
        if( (moveX && p == pCircle.getPlayer()) || (!moveX && p == pCross.getPlayer())){
            //the wrong person tried to move >:(
            return false;
        }
        if(board != activeBoard && activeBoard!=-1){
            //they tried to put a move on a board that isnt active! Scandalous!
            return false;
        }
        //lets get the board, and figure out if it should be an X or an O
        TTTInstance instance = getBoard(board);
        boolean cross = (p==pCross.getPlayer()); //i cant remember where i needed this for, but it works!
        return instance.move(row, column, cross, moveX ? pCross.getPrimaryPawn() : pCircle.getPawn(pCross.getPrimaryPawn()));
    }

    /**
     * Resets the entire board
     */
    private void reset(){
        //resets each instance first
        for(TTTInstance[] ins: board){
            for(TTTInstance instance: ins){
                instance.resetBoard();
            }
        }

        //resets the names on the sides
        new NameBuilder("null", true, this.gameNumber).reset();
        new NameBuilder("null", false, this.gameNumber).reset();

        //reset the giant pawn things
        for(int y = 103; y >= 75; y-- ){
            for(int z= 249; z >= 221; z--){
                setBlock(292 + (space*gameNumber), y, z, Material.AIR);
                setBlock(406 + (space*gameNumber), y, z, Material.AIR);
            }
        }

        //unregister all events, as we dont need them anymore
        PlayerInteractEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
        //cancel all tasks, as they arent needed anymore
        Bukkit.getScheduler().cancelTask(lookRun);
        Bukkit.getScheduler().cancelTask(particles);

        //let the main person know this game has ended, so it can be opened up again
        TicTacToeCommand.endGame(this.gameNumber);
    }

    /**
     * Gets the arena number
     * @return ArenaNumber
     */
    public int getGameNumber(){
        return gameNumber;
    }

    /**
     * Gets the X user
     * @return TTTPlayer of the X user
     */
    public TTTPlayer getCrossPlayer(){
        return pCross;
    }

    /**
     * Gets the O user
     * @return TTTPlayer of the O user
     */
    public TTTPlayer getCirclePlayer(){
        return pCircle;
    }

    /**
     *
     * @return The player who's current turn it is
     */
    public TTTPlayer getPlayerTurn(){
        return moveX ? pCross : pCircle;
    }

    /**
     * Calculates the total moves used in this game
     * @return total moves used in the game
     */
    public int getTotalMoves(){
        int moves = 0;
        for(int i = 0; i < 9; i++){
            TTTInstance instance = getBoard(i);
            moves += instance.getFilledSquares();
        }
        return moves;
    }

    public String getSpectators(){
        String output  = "";
        for(Player p :spectators){
            output += p.getDisplayName() + ",";
        }
        return output;
    }

    /**
     *
     * @return StartTime in milliseconds
     */
    public long getStartTime(){
        return startTime;
    }
}
