package com.skyerzz.tictactoe.game.sound;

import com.skyerzz.tictactoe.TicTacToe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sky on 17-2-2018.
 */
public class Song {

    /**
     * Stores the known songs
     */
    private static HashMap<String, ArrayList<Note>> library = new HashMap<>();

    /**
     * Indexes a song to the library
     * @param fileName Name of the song (file) to be read. Files must be in /files/songs/ [song] .txt
     */
    private static void indexSong(String fileName){
        BufferedReader br;
        System.out.println(Song.class);
        System.out.println(Song.class.getResourceAsStream("/files/songs/" + fileName + ".txt"));
        InputStream in = Song.class.getResourceAsStream("/files/songs/" + fileName + ".txt");
        if(in==null){
            System.out.println("Couldnt find /files/songs/" + fileName + ".txt");
        }
        br = new BufferedReader(new InputStreamReader(in));

        ArrayList<Note> notes = new ArrayList<>();
        String line;
        try {
            while((line = br.readLine())!=null){
                //System.out.println("Found line " + line + " adding note...");
                notes.add(Note.parse(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("adding song " + fileName);
        library.put(fileName, notes);
    }

    /**
     * Plays a song to a player
     * @param song Song to be played
     * @param p Player to hear the song
     */
    public static void playSong(String song, Player p){
        if(!library.containsKey(song)){
            System.out.println("Couldnt find song " + song + ". indexing...");
            indexSong(song);
        }
        final ArrayList<Note> notes = library.get(song);
        if(notes==null){
            System.out.println("Notes == null!");
            return;
        }
        int[] x= new int[1];
        x[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(TicTacToe.pluginInstance, new Runnable() {

            int index = 0;

            @Override
            public void run() {
                if(index>=notes.size()-1){
                    Bukkit.getScheduler().cancelTask(x[0]);
                }
                notes.get(index++).playSound(p);
            }
        }, 1L, 2L);
    }
}
