package com.skyerzz.tictactoe.game;

/**
 * Easy access particle colors
 * Created by sky on 5-9-2017.
 */
public enum ParticleColor {
    RED(0f, 0f, 0f),
    GREEN(-1f, 1f, 0f),
    BLUE(-1f, 0f, 1f),
    WHITE(0f, 1f, 1f),
    BLACK(-1f,0,0);

    private float one, two, three;

    //note RED value is red value MINUS ONE. Minecraft just works that way.

    /**
     *
     * @param one float value for R(GB) value
     * @param two float value for (R)G(B) value
     * @param three float value for (RG)B value
     */
    ParticleColor(float one, float two, float three){
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public float getOne(){ return one; }
    public float getTwo(){ return two; }
    public float getThree(){ return three; }
}
