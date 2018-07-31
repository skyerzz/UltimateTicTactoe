package com.skyerzz.tictactoe;

import com.skyerzz.tictactoe.game.TTTBoard;
import com.skyerzz.tictactoe.game.shop.*;
import com.skyerzz.tictactoe.game.sound.Song;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.chat.*;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Command Handler
 * Created by sky on 7-8-2017.
 */
public class TicTacToeCommand implements CommandExecutor {

    private static ArrayList<TTTBoard> gameInstances = new ArrayList();
    private static HashMap<UUID, UUID> challenges = new HashMap<>();

    private boolean isActive = true; //emergency stop protocol
    private int maxGames = 10;


    /**
     * See the google docs file for all commands and their functions
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length < 1){
            return false;
        }
        //codes: OP = op user, P = player, -C = console, -U = underlying

        //<editor-fold desc="[OP-C][Emergency Stop commands & check]">
        if(args[0].equalsIgnoreCase("stop") && commandSender.isOp()){
            this.isActive = false;
            commandSender.sendMessage("[TTT] Emergency stop activated! Killing all games, and disallowing future games...");
            interruptAll();
            return true;
        }
        if(args[0].equalsIgnoreCase("start") && commandSender.isOp()){
            this.isActive = true;
            commandSender.sendMessage("[TTT] Starting up! Allowing games again!");
            return true;
        }
        if(!isActive){
            commandSender.sendMessage("[TTT] TTT is in an Emergency Stop!");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[P-C] [Info]">
        if(args[0].equalsIgnoreCase("info")){
            commandSender.sendMessage("Running Ultimate Tic Tac Toe version " + TicTacToe.version + " by skyerzz");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[OP-C][Reset]">
        if(args[0].equalsIgnoreCase("reset") && commandSender.isOp()){
            if(args.length > 1){
                if(args[1].equalsIgnoreCase("queue")){
                    commandSender.sendMessage("[TTT] Reset the Queue, removing " + challenges.size() *2 + " people!");
                    challenges.clear();
                    return true;
                }


                //resetting of boards
                if(args.length < 3){
                    commandSender.sendMessage("[TTT] Usage: /ttt reset <board | player> <# | playerName>");
                    return true;
                }
                if(args[1].equalsIgnoreCase("board")){
                    int game;
                    try {
                        game = Integer.parseInt(args[2]);
                    }catch(NumberFormatException e){
                        commandSender.sendMessage("[TTT] Invalid board number!");
                        return true;
                    }
                    if(game > maxGames){
                        commandSender.sendMessage("[TTT] Board cannot be bigger than max amount of boards (" + maxGames + ")");
                        return true;
                    }
                    for(TTTBoard board: gameInstances){
                        if(board.getGameNumber() == game){
                            board.interrupt();
                            commandSender.sendMessage("[TTT] Reset board " + game + "!");
                            gameInstances.remove(board);
                            return true;
                        }
                    }
                    new TTTBoard(game).interrupt();
                    commandSender.sendMessage("[TTT] Created and Reset board " + game + "!");
                    return true;
                }else if(args[1].equalsIgnoreCase("player")){
                    for(TTTBoard board: gameInstances){
                        if(board.getCrossPlayer().getPlayer().getName().equalsIgnoreCase(args[2]) || board.getCirclePlayer().getPlayer().getName().toLowerCase().equalsIgnoreCase(args[2])){
                            commandSender.sendMessage("[TTT] Reset board " + board.getGameNumber() + "!");
                            board.interrupt();
                            gameInstances.remove(board);
                            return true;
                        }
                    }
                    commandSender.sendMessage("[TTT] Could not find a board with player " + args[2]);
                    return true;
                }else if(args[1].equalsIgnoreCase("all")){
                    interruptAll();
                    commandSender.sendMessage("[TTT] Reset all games!");
                    return true;
                }
            }
            commandSender.sendMessage("[TTT] Usage: /ttt reset <board | player> <# | playerName>");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[P-c][Games command]">
        if(args[0].equalsIgnoreCase("games")){
            int i = 0;
            commandSender.sendMessage("[TTT] Current ongoing games:");
            for(TTTBoard board: gameInstances){
                commandSender.sendMessage("Board " + i++ + ": " + board.getCirclePlayer().getPlayer().getName() + " VS. " + board.getCrossPlayer().getPlayer().getName() + ". Turn: " + board.getPlayerTurn().getPlayer().getName()  + ". \nspectators: " + board.getSpectators());
            }
            commandSender.sendMessage("[TTT] Current Queue: ");
            for(UUID uuid: challenges.keySet()){
                commandSender.sendMessage("[TTT] " + uuid.toString() + " -> " + challenges.get(uuid).toString());
            }
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[OP/P-C][Coin Commands]">
        if(args[0].equalsIgnoreCase("setcoins") && commandSender.isOp()){
            if(args.length < 3){
                commandSender.sendMessage("Not enough Arguments!");
                return true;
            }
            Player p = (Player) Bukkit.getOfflinePlayer(args[1]);
            if(p==null){
                commandSender.sendMessage("Couldnt find that player in the system!");
                return true;
            }
            int amount;
            try{
                amount = Integer.parseInt(args[2]);
            }catch(NumberFormatException e){
                commandSender.sendMessage("Please fill in a valid integer number");
                return true;
            }
            new TTTPlayer(p).setCoins(amount);
            commandSender.sendMessage("Set the coins of player " + args[1] + " to " + args[2]);
            return true;
        }

        if(args[0].equalsIgnoreCase("getcoins")){
            if(args.length < 2){
                commandSender.sendMessage("Not enough Arguments!");
                return true;
            }
            Player p = (Player) Bukkit.getOfflinePlayer(args[1]);
            if(p==null){
                commandSender.sendMessage("Couldnt find that player in the system!");
                return true;
            }
            int coins = new TTTPlayer(p).getCoins();
            commandSender.sendMessage("[TTT]" + args[1] + " has  " + coins + " coins.");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[OP-C][NPC Killer Command]">
        if(args[0].equalsIgnoreCase("killallnpcs") && commandSender.isOp()){
            commandSender.sendMessage("[TTT] WE KILLED EVERYONE. I HOPE YOU'RE HAPPY.");
            TTTNPC.killAllInstances();
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[OP-C][Force Game Command]">
        if(args[0].equalsIgnoreCase("forcegame")){
            if(args.length < 3){
                commandSender.sendMessage("[TTT] Usage: /ttt forcegame <player> <player> [board]");
                return true;
            }
            Player one = Bukkit.getPlayer(args[1]);
            Player two = Bukkit.getPlayer(args[2]);
            if(one == null || two == null || !one.isOnline() || !two.isOnline()){
                commandSender.sendMessage("[TTT] Can't force this game because one of the 2 given players is not online!");
                return true;
            }
            int board = -1;
            if(args.length > 3){
                try{
                    board = Integer.parseInt(args[3]);
                    if(board >= maxGames || board < 0){
                        commandSender.sendMessage("[TTT] Please pick a board number between 0 and " + (maxGames-1));
                        return true;
                    }
                }catch(NumberFormatException e){
                    commandSender.sendMessage("[TTT] Please fill in a valid number for the board.");
                    return true;
                }
            }
            if(board!=-1){
                forceStartGame(one, two, board);
                return true;
            }
            startGame(one, two);
            return true;
        }
        //</editor-fold>

        //no more commands from console after this
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("[TTT] consoles not allowed to do this >:(");
            return true;
        }

        Player p = (Player) commandSender;
        //<editor-fold desc="[P-U][Shop Command]">
        if(args[0].equalsIgnoreCase("shop")){
            if(args.length < 2){
                p.sendMessage("Not enough Arguments!");
                return true;
            }
            switch(args[1].toLowerCase()){
                case "main":
                    new MainWindow(new TTTPlayer(p)).open();
                    break;
                case "pawns":
                    new PawnShopWindow(new TTTPlayer(p)).open();
                    break;
                case "challenge":
                    new ChallengeShopWindow(new TTTPlayer(p)).open();
                    break;
                case "material":
                    new MaterialShopWindow(new TTTPlayer(p)).open();
                    break;
                default:
                    p.sendMessage("[TTT] Couldnt find that shop name!");
            }
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[OP][NPC Spawn Command]">
        if(args[0].equalsIgnoreCase("spawnnpc") && p.isOp()){
            new TTTNPC(p.getLocation());
            p.sendMessage("[TTT] Spawned a new NPC on your location! NPC will permanently die on server restart.");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[P][Spectate Commands]">
        if(args[0].equalsIgnoreCase("spectate")){
            if(args.length < 2){
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if(player == null || !player.isOnline()){
                try{
                    int number = Integer.parseInt(args[1]);
                    for(TTTBoard board:gameInstances){
                        if(board.getGameNumber() == number){
                            p.sendMessage("[TTT] Sending you to specate....");
                            board.addSpectator(p);
                            return true;
                        }
                    }
                }catch(NumberFormatException e){
                    //we dont have to catch this as we state to the player something was wrong below here
                }
                p.sendMessage("[TTT] This player doesnt exist, or is not online, or you havent filled in an active board number!");
                return true;
            }
            for(TTTBoard board:gameInstances){
                if(board.getCirclePlayer().getPlayer().getUniqueId()==player.getUniqueId() || board.getCrossPlayer().getPlayer().getUniqueId()==player.getUniqueId()){
                    p.sendMessage("[TTT] Sending you to specate....");
                    board.addSpectator(p);
                    return true;
                }
            }
            p.sendMessage("[TTT] Couldnt find a game with that player!");
            return true;
        }

        if(args[0].equalsIgnoreCase("stopspectate")){
            for(TTTBoard board:gameInstances){
                if(board.removeSpectator(p)){
                    return true;
                }
            }
            p.sendMessage("[TTT] Couldn't remove you as a spectator because you arent spectating anyone!");
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[P-U][Invite & Accept commands]">
        if(args[0].equalsIgnoreCase("challenge") || args[0].equalsIgnoreCase("invite")){
            if(args.length < 2){
                 return false;
            }

            Player player = Bukkit.getPlayer(args[1]);
            challengePlayer(p, player);

            return true;
        }

        if(args[0].equalsIgnoreCase("accept")){
            if(args.length < 2){
                return false;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if(isInGame(player) || isInGame(p)){
                p.sendMessage("[TTT] Cannot start a new game if one of you is in a game!");
                challenges.remove(player.getUniqueId());
                return true;
            }
            if(!challenges.containsKey(player.getUniqueId())){
                p.sendMessage("[TTT] You have not been challenged by that player!");
                return true;
            }
            challenges.remove(player.getUniqueId());
            startGame(p, player);
            p.sendMessage("[TTT] Starting a game...");
            return true;

        }
        //</editor-fold>

        //<editor-fold desc="[P-U][Tutorial command]">
        if(args[0].equalsIgnoreCase("tutorial")){
            ItemStack book = GeneralShopItem.getHowToPlayBook();
            p.getInventory().setHeldItemSlot(0);
            p.getInventory().setItem(0, book);
            //i found this on the web, not sure what it does but it works!
            ByteBuf buf = Unpooled.buffer(256);
            buf.setByte(0, (byte) 0);
            buf.writerIndex(1);

            PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
            p.getInventory().setItem(0, null);
            return true;
        }
        //</editor-fold>

        //<editor-fold desc="[P][Statistics command]">
        if(args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("statistics")){
            if(args.length < 2){
                return false;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if(player==null || !player.hasPlayedBefore() || player.getUniqueId()==null){
                p.sendMessage("This player does not exist on the server!");
                return true;
            }
            TTTPlayer tttPlayer = new TTTPlayer(player.getPlayer());
            p.sendMessage("[TTT] Stats for player " + args[1] + "\n" +
                        "Wins: " + tttPlayer.getIntValue("wins") + "\n" +
                        "Losses: " + tttPlayer.getIntValue("losses") + "\n" +
                        "Draws: " + tttPlayer.getIntValue("draws"));
            return true;
        }
        //</editor-fold>


        Player player = Bukkit.getPlayer(args[0]);
        challengePlayer(p, player);
        return true;
    }



    /**
     * Interrupts all currently online gameinstances
     */
    public static void interruptAll(){
        //reset all boards (through clone of the list, as the interrupt removes it from the list
        ((ArrayList<TTTBoard>) gameInstances.clone()).forEach(TTTBoard::interrupt);
        //purge the lists we have here
        challenges.clear();
        gameInstances.clear();
    }

    /**
     * Finds out if a player is already in a game
     * @param player Player to be checked
     * @return True if in game, false if not in game
     */
    private boolean isInGame(Player player){
        for(TTTBoard board: gameInstances){
            if(board.getCrossPlayer().getPlayer().getUniqueId() == player.getUniqueId() || board.getCirclePlayer().getPlayer().getUniqueId() == player.getUniqueId()){
                return true;
            }
        }
        return false;
    }

    /**
     * Sends a callenge to the Challenged player
     * @param challenger The player who created the challenge
     * @param challenged The player who is sent the challenge
     */
    private void challengePlayer(Player challenger, Player challenged){
        if(challenged==null || !challenged.isOnline()){
            challenger.sendMessage("[TTT] This player is not online!");
            return;
        }
        if(isInGame(challenged) || isInGame(challenger)){
            challenger.sendMessage("[TTT] You or the challenged player is already in a game!");
            return;
        }
        if(challenger.getUniqueId() == challenged.getUniqueId()){
            challenger.sendMessage("[TTT] You cant play against yourself!");
            return;
        }
        challenges.put(challenger.getUniqueId(), challenged.getUniqueId());
        TextComponent message = new TextComponent("[TTT] \u00A7a" + challenger.getName() + " Challenged you to an ultimate Tic Tac Toe duel! \n[TTT] \u00A7aClick Here to accept!");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("\u00A7aAccept " + challenger.getName() + "'s challenge").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ttt accept " + challenger.getName()));
        challenged.spigot().sendMessage(message);
        challenger.sendMessage("[TTT] Challenged " + challenged.getName() + " to an Ultimate Tic Tac Toe Duel!");
    }

    /**
     * Starts a new game between the 2 given players
     * @param accepter Player 1 (who accepted the challenge)
     * @param challenger Player 2 (who challenged player1)
     */
    private void startGame(Player accepter, Player challenger){
        int newNumber = 0;
        for(int i = 0; i < maxGames; i ++){
            for(TTTBoard board:gameInstances){
                if(board.getGameNumber()==i){
                    newNumber=-1;
                    break;
                }
                newNumber = i;
            }
            if(newNumber!=-1){
                break;
            }
        }
        if(newNumber==-1){
            return; //help!
        }
        accepter.sendMessage("[TTT] Starting game...");
        challenger.sendMessage("[TTT] Starting game...");
        gameInstances.add(new TTTBoard(new TTTPlayer(challenger), new TTTPlayer(accepter), newNumber));
    }

    private boolean forceStartGame(Player one, Player two, int board){
        for(TTTBoard b:gameInstances){
            if(b.getGameNumber()==board){
                return false;
            }
        }
        gameInstances.add(new TTTBoard(new TTTPlayer(one), new TTTPlayer(two), board));
        return true;

    }

    /**
     * Ends the game on the given arena
     * @param arena Arena to end the game at
     */
    public static void endGame(int arena){
        TTTBoard toRemove = null;
        for(TTTBoard board:gameInstances){
            if(board.getGameNumber() == arena){
                toRemove = board;
                break;
            }
        }
        if(toRemove==null){
            return; //wasnt a game!
        }
        gameInstances.remove(toRemove);
    }
}
