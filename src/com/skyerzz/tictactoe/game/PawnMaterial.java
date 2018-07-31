package com.skyerzz.tictactoe.game;

import org.bukkit.Material;

/**
 * Materials of pawns
 * Created by sky on 2-11-2017.
 */
public enum PawnMaterial {
    //planks
    OAK_WOOD("Oak wood planks", Material.WOOD),
    SPRUCE_WOOD("Spruce wood planks", Material.WOOD, (byte) 1),
    BIRCH_WOOD("Birch wood planks", Material.WOOD, (byte) 2),
    JUNGLE_WOOD("Jungle wood planks", Material.WOOD, (byte) 3),
    ACACIA_WOOD("Acacia wood planks", Material.WOOD, (byte) 4),
    DARK_OAK_WOOD("Dark oak wood planks", Material.WOOD, (byte) 5),

    //stones
    GRANITE("Polished granite", Material.STONE, (byte) 2),
    DIORITE("Polished diorite", Material.STONE, (byte) 4),
    ANDESITE("Polished andesite", Material.STONE, (byte) 6),

    //minerals
    COAL("Coal", Material.COAL_BLOCK),
    QUARTZ("Quartz", Material.QUARTZ_BLOCK),
    LAPIS("Lapis Lazuli", Material.LAPIS_BLOCK),
    GOLD("Gold", Material.GOLD_BLOCK),
    IRON("Iron", Material.IRON_BLOCK),
    OBSIDIAN("Obsidian", Material.OBSIDIAN),
    REDSTONE("Redstone", Material.REDSTONE_BLOCK),

    //others
    LEAVES("Leaves", Material.LEAVES),
    BRICKS("Bricks", Material.BRICK),
    RED_MUSHROOM("Red mushroom", Material.HUGE_MUSHROOM_2, (byte) 14),
    SEA_LANTERN("Sea lantern", Material.SEA_LANTERN),
    ENDSTONE("Endstone", Material.ENDER_STONE),
    PRISMARINE("Prismarine", Material.PRISMARINE),
    DARK_PRISMARINE("Dark prismarine", Material.PRISMARINE, (byte) 2),
    HAY("Haybale", Material.HAY_BLOCK);

    private Material material;
    private byte materialData;
    private String displayName;


    /**
     * short-hand for objects without materialdata
     * @param displayName DisplayName of the material
     * @param material Material of this material
     */
    PawnMaterial(String displayName, Material material){
        this(displayName, material, (byte) 0);
    }

    /**
     *
     * @param material Material of this pawnMaterial
     * @param materialData byte Material data of this pawnMaterial
     */
    PawnMaterial(String displayName, Material material, byte materialData){
        this.material = material;
        this.materialData = materialData;
        this.displayName = displayName;
    }

    /**
     * Returns this pawnMaterial's material value
     * @return Material
     */
    public Material getMaterial(){ return material; }

    /**
     * Returns this pawnMaterial's data value
     * @return byte MaterialData
     */
    public byte getMaterialData(){ return materialData; }

    /**
     * Returns the name used in every display for this pawnMaterial
     * @return String displayname
     */
    public String getDisplayName(){ return displayName; }

    /**
     * Finds the pawnMaterial matching the given displayname
     * @param displayName The displayname of the to be found PawnMaterial
     * @return PawnMaterial belonging to the given displayname, or null if non-existent.
     */
    public static PawnMaterial getPawnMaterialByDisplayName(String displayName){
        for(PawnMaterial pawnMaterial:PawnMaterial.values()){
            if(pawnMaterial.getDisplayName().equals(displayName)){
                return pawnMaterial;
            }
        }
        return null;
    }

}
