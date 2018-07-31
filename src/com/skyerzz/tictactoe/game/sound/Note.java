package com.skyerzz.tictactoe.game.sound;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by sky on 17-2-2018.
 */
public class Note {

    private Sound sound;
    private float volume, pitch;

    /**
     * Creates a Note for a song
     * @param sound Sound Effect
     * @param volume volume (float x.x)
     * @param pitch pitch (float x.x)
     */
    public Note(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Plays the sound to the player
     * @param p Player to play sound to
     */
    public void playSound(Player p){
        if(sound!=null) {
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Reads a note from a string line
     * @param s String to read from
     * @return A Note
     */
    public static Note parse(String s){
        String[] arr = s.split(",");
        //System.out.println("Sound: " + arr[0]);
        Sound f = parseSound(arr[0]);
        float vol,pitch;
        try{
            vol = Float.parseFloat(arr[1]);
            //System.out.println("vol: " + vol);
            pitch = Float.parseFloat(arr[2]);
            //System.out.println("pitch: " + pitch);
        }catch(NumberFormatException e){
            System.out.println("Wrong number format!");
            return new Note(null, 0 , 0);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Array Not long enough!");
            return new Note(null, 0 , 0);
        }
        return new Note(f, vol, pitch);
    }

    /**
     * Returns a sound, if available. Null if not available.
     * @param s Sound to be returned
     * @return Sound of input
     */
    private static Sound parseSound(String s){
        switch(s.toLowerCase()){
            case "piano":
                return Sound.NOTE_PIANO;
            case "pling":
                return Sound.NOTE_PLING;
            case "bass":
                return Sound.NOTE_BASS;
            case "bass_drum":
                return Sound.NOTE_BASS_DRUM;
            case "drum":
                return Sound.NOTE_SNARE_DRUM;
            case "stick":
                return Sound.NOTE_STICKS;
            case "guitar":
                return Sound.NOTE_BASS_GUITAR;
            case "exp":
                return Sound.ORB_PICKUP;
        }
        return null;
    }
}
