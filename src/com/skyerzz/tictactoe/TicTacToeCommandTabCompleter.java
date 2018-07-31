package com.skyerzz.tictactoe;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tab completion for the command
 * Created by sky on 2-11-2017.
 */
class TicTacToeCommandTabCompleter implements TabCompleter{

    private static List<String> baseArgsDefault = Arrays.asList("accept", "challenge", "games", "spectate", "stats");
    private static List<String> baseArgsOp = Arrays.asList("accept", "challenge", "forcegame", "games", "killallNPCs", "reset", "shop", "spawnNPC", "spectate", "start", "stats", "stop");
    private static List<String> shopArgs = Arrays.asList("Challenge", "Main", "Material", "Pawns" );
    private static List<String> resetArgs = Arrays.asList("all", "board", "player", "queue");


    /**
     * Implements tab completion for the TicTacToeCommand
     */
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if(!command.getName().equalsIgnoreCase("tictactoe")){
            //not our command somehow?
            return null;
        }
        if(!(commandSender instanceof Player)){
            //non-players dont get fancy tabs!
            return null;
        }
        Player p = (Player) commandSender;
        final List<String> matches = new ArrayList<>();
        if(args.length==1){
            if(p.isOp()){
                return StringUtil.copyPartialMatches(args[0], baseArgsOp, matches);
            }
            return StringUtil.copyPartialMatches(args[0], baseArgsDefault, matches);
        }
        if(args.length==2 && p.isOp()){
            switch(args[0].toLowerCase().trim()){
                case "shop":
                    return StringUtil.copyPartialMatches(args[1], shopArgs, matches);
                case "reset":
                    return StringUtil.copyPartialMatches(args[1], resetArgs, matches);
                default:
                    return null;
            }
        }
        return null;
    }
}
