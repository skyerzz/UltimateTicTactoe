package com.skyerzz.tictactoe.game.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Manager for all sounds used in games.
 * Created by sky on 8-10-2017.
 */
public class SoundManager {

    /**
     * Plays a "Something Went Wrong" sound to the given player
     * @author sky
     * @param p Player to play the sound for
     */
    public static void playSomethingWentWrongSound(Player p){
        p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 0.3f);
    }

    /**
     * Plays a _caching_ sound to the given player
     * @author sky
     * @param p Player to play the sound for
     */
    public static void playCachingSound(Player p){
        p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1f, 1.3f);
    }

    /**
     * Plays the sound made when a successfull move is made by said player
     * @author sky
     * @param p Player to play the sound for
     */
    public static void playMoveSound(Player p){ Song.playSong("succesMove", p);}

    /**
     * Plays the sound made when a successfull move is made by the opponent player
     * @author sky
     * @param p Player to play the sound for
     */
    public static void playMoveDoneSound(Player p){ Song.playSong("newMove", p);}
}
