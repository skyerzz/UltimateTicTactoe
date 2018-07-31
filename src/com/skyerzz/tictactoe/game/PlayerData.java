package com.skyerzz.tictactoe.game;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Player Class, stores and retrieves data.
 * Created by sky on 7-10-2017.
 */
public class PlayerData {

    /** UUID for the player */
    private UUID uuid;
    /** The folder where player's data should be at */
    private final String baseFolder = "playerData/UltimateTicTacToe/";
    private JsonObject jsonFile;

    /**
     * Instances the class
     * @param playerUUID UUID of the player who's data is needed
     */
    public PlayerData(UUID playerUUID){
        this.uuid = playerUUID;
        initDirect();
        initFile();
    }

    /**
     * Checks if the base folder exists. If not, create it
     */
    private void initDirect(){
        File file = new File(baseFolder);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * Checks if a file exists for the player. If not, create a new file for the player
     */
    private void initFile(){
        File file = new File(baseFolder + getStrippedUUID(uuid) + ".json");
        if(!file.exists()){
            try {
                //the file didnt exist :/ Lets welcome this new player, and create a file for them!
                //log it just to be sure
                System.out.println("[TTT] Creating new PlayerData file at " + file.getName());
                //create an empty file so we dont get errors later
                file.createNewFile();
                //instance a new file!
                createNewFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //lets set the file to our current jsonFile object, for easy editing.
        this.jsonFile = new JsonParser().parse(getTextFromFile(file)).getAsJsonObject();
    }

    /**
     * Reads the file and returns it as a String. Needed to get it into JSON format later.
     * @param file File to get the text from
     * @return String of the file, or null if an error happened
     */
    private String getTextFromFile(File file){
        BufferedReader reader = null;
        try {
            //lets read a file!
            reader = new BufferedReader(new FileReader(file));
            String text = "", line;

            while((line = reader.readLine()) != null){
                text += line + "\n";
            }
            reader.close();
            //well that was easy. Lets return our text!
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try{
                //close off the reader finally, we dont need it anymore
                reader.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Creates a new file for a player, and sets the default values
     * @param file File to create
     */
    private void createNewFile(File file){
        JsonObject main = new JsonObject();

        ArrayList<String> pawns = new ArrayList<>();
        //lets give them 2 default pawns :)
        pawns.add(Pawn.X.name());
        pawns.add(Pawn.O.name());
        Gson gson = new Gson();
        main.add("pawns", gson.toJsonTree(pawns).getAsJsonArray());
        //lets give them a coin field too
        main.addProperty("coins", 0);
        //to ensure they can instantly play, set their pawns too!
        main.addProperty("PrimaryPawn", Pawn.X.name());
        main.addProperty("SecondaryPawn", Pawn.O.name());
        main.addProperty("primaryMaterial", PawnMaterial.OAK_WOOD.getMaterial().toString());
        main.addProperty("primaryMaterialdata", PawnMaterial.OAK_WOOD.getMaterialData());
        main.addProperty("secondaryMaterial", PawnMaterial.SPRUCE_WOOD.getMaterial().toString());
        main.addProperty("secondaryMaterialdata", PawnMaterial.SPRUCE_WOOD.getMaterialData());
        main.add("games", gson.toJsonTree(new ArrayList<JsonObject>()).getAsJsonArray());

        try(FileWriter f = new FileWriter(file)){
            f.write(main.toString());
            f.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves the jsonFile to a file on the system
     */
    private void saveFile(){
        File file = new File(baseFolder + getStrippedUUID(uuid) + ".json");
        try(FileWriter f = new FileWriter(file)){
            f.write(this.jsonFile.toString());
            f.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an unlocked pawn to the file
     * @param p Pawn to be added
     */
    public void addPawn(Pawn p){
        JsonArray array = jsonFile.get("pawns").getAsJsonArray();
        Gson gson = new Gson();
        ArrayList<String> pawns = gson.fromJson(array, new TypeToken<List<String>>(){}.getType());
        pawns.add(p.name());
        jsonFile.add("pawns", gson.toJsonTree(pawns).getAsJsonArray());
        saveFile();
    }

    /**
     * Strips uuid's from their '-' chars
     * @param uuid UUID as input
     * @return the stripped uuid
     */
    private String getStrippedUUID(UUID uuid){
        return uuid.toString().replace("-", "");
    }

    /**
     * Gets the current selected X-type pawn
     * @return Pawn (type X)
     */
    public Pawn getPrimaryPawn(){
        return Pawn.valueOf(jsonFile.get("PrimaryPawn").getAsString());
    }

    /**
     * Gets the current selected O-type pawn
     * @return Pawn (type O)
     */
    public Pawn getSecondaryPawn(){
        return Pawn.valueOf(jsonFile.get("SecondaryPawn").getAsString());
    }

    /**
     * Gets the int value from the json using the Key
     * @param key Key to get from the json
     * @return int-value of the key
     */
    public int getIntValue(String key){
        if(jsonFile.get(key)==null || jsonFile.get(key).isJsonNull()){
            return 0;
        }
        return jsonFile.get(key).getAsInt();
    }

    /**
     * Sets a key,value pair in the json
     * @param key Key to set
     * @param value Value to set to the key
     */
    public void setIntValue(String key, int value){
        jsonFile.addProperty(key, value);
        saveFile();
    }

    /**
     * Gets all unlocked pawns of the player
     * @return ArrayList of unlocked pawns
     */
    public ArrayList<Pawn> getUnlockedPawns(){
        ArrayList<Pawn> pawns = new ArrayList<>();
        for(JsonElement e: jsonFile.get("pawns").getAsJsonArray()){
            Pawn p = Pawn.valueOf(e.getAsString());
            pawns.add(p);
        }
        return pawns;
    }

    /**
     * Sets an X-type pawn
     * @param p Pawn to set
     */
    public void setPrimaryPawn(Pawn p){
        jsonFile.addProperty("PrimaryPawn", p.name());
        saveFile();
    }

    /**
     * Sets an O-type pawn
     * @param p Pawn to set
     */
    public void setSecondaryPawn(Pawn p){
        jsonFile.addProperty("SecondaryPawn", p.name());
        saveFile();
    }

    /**
     * Gets all previous played games as JsonObjects
     * @return ArrayList of JsonObjects
     */
    private ArrayList<JsonObject> getPreviousGames(){
        ArrayList<JsonObject> objects = new ArrayList<>();
        for(JsonElement element: jsonFile.get("games").getAsJsonArray()){
            objects.add(element.getAsJsonObject());
        }
        return objects;
    }

    /**
     * Adds a new gameinstance to the previously played games
     * @param data GameData from game
     */
    public void addGameInstance(GameData data){
        //get all previous games first
        ArrayList<JsonObject> objects = getPreviousGames();
        //add the new jsonObject from the gameData to the arraylist
        objects.add(data.getAsJsonObject());
        Gson gson = new Gson();
        //set the games back to the jsonfile
        jsonFile.add("games", gson.toJsonTree(objects).getAsJsonArray());
        saveFile();
    }

    /**
     * Returns the Material for pawns
     * @return Material for pawns
     */
    public Material getPrimaryMaterial(){
        return Material.getMaterial(jsonFile.get("primaryMaterial").getAsString());
    }

    /**
     * Sets the material used for pawns
     * @param material Material
     */
    public void setPrimaryMaterial(Material material){
        jsonFile.addProperty("primaryMaterial", material.name());
        saveFile();
    } 
    
    /**
     * Returns the Material for pawns
     * @return Material for pawns
     */
    public Material getSecondaryMaterial(){
        return Material.getMaterial(jsonFile.get("secondaryMaterial").getAsString());
    }

    /**
     * Sets the material used for pawns
     * @param material Material
     */
    public void setSecondaryMaterial(Material material){
        jsonFile.addProperty("secondaryMaterial", material.name());
        saveFile();
    }

    /**
     * Returns the material Data for the pawn blocks
     * @return byte material data
     */
    public byte getPrimaryMaterialData(){
        return jsonFile.get("primaryMaterialdata").getAsByte();
    }

    /**
     * Sets the material Data for the pawn blocks
     * @param data Material Data
     */
    public void setPrimaryMaterialData(byte data){
        jsonFile.addProperty("primaryMaterialdata", data);
        saveFile();
    }

    /**
     * Returns the material Data for the pawn blocks
     * @return byte material data
     */
    public byte getSecondaryMaterialData(){
        return jsonFile.get("secondaryMaterialdata").getAsByte();
    }

    /**
     * Sets the material Data for the pawn blocks
     * @param data Material Data
     */
    public void setSecondaryMaterialData(byte data){
        jsonFile.addProperty("secondaryMaterialdata", data);
        saveFile();
    }
}
