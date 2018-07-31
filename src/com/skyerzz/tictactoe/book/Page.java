package com.skyerzz.tictactoe.book;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Pages to be added to a book
 * Created by sky on 2-11-2017.
 */
public class Page {

    private List<BaseComponent> textLines;

    /**
     * @author sky
     *
     * Creates a new page
     */
    public Page(){
        textLines = new ArrayList<>();
    }

    /**
     * Add empty lines
     *
     * @author sky
     * @param amount amuont of empty lines to be added
     */
    public void addNewLines(int amount){
        while(amount-- > 0){
            addText("\n", null, null, ChatColor.BLACK);
        }
    }

    /**
     * Adds a string of text with the selected properties
     *
     * @author sky
     * @param text text to be added
     * @param properties properties of the text
     */
    public void addText(String text, Property...properties){
        addText(text, null, null, null, properties);
    }

    /**
     * Adds a colored string of text with properties
     *
     * @author sky
     * @param text Text to be added
     * @param color Color of the text
     * @param properties Properties of the text
     */
    public void addText(String text, ChatColor color,Property...properties){
        addText(text, null, null, color, properties);
    }

    /**
     * Adds a colored, clickable string of text with properties
     *
     * @author sky
     * @param text Text to be added
     * @param clickEvent Clickevent shipped with the text
     * @param color Color of the text
     * @param properties Properties of the text
     */
    public void addText(String text, ClickEvent clickEvent, ChatColor color, Property...properties){
        addText(text, clickEvent, null, color, properties);
    }

    /**
     * Adds a colored, hoverable string of text with properties
     *
     * @author sky
     * @param text Text to be added
     * @param hoverEvent HoverEvent to be shipped with the text
     * @param color Color of the text
     * @param properties Properties of the text
     */
    public void addText(String text, HoverEvent hoverEvent, ChatColor color, Property...properties){
        addText(text, null, hoverEvent, color, properties);
    }

    /**
     * Adds a colored, clickable, hoverable string of text with properties
     *
     * @author sky
     * @param text Text to be added
     * @param clickEvent ClickEvent to be shipped with the text
     * @param hoverEvent HoverEvent to be shipped with the text
     * @param color Color of the text
     * @param properties Properties of the text
     */
    public void addText(String text, ClickEvent clickEvent, HoverEvent hoverEvent, ChatColor color, Property...properties){
        TextComponent line = new TextComponent(text);
        line.setClickEvent(clickEvent);
        line.setHoverEvent(hoverEvent);
        line.setColor(color);
        for(Property property: properties){
            switch(property){
                case BOLD:
                    line.setBold(true);
                    break;
                case ITALIC:
                    line.setItalic(true);
                    break;
                case OBFUSCATED:
                    line.setObfuscated(true);
                    break;
                case STRIKETHROUGH:
                    line.setStrikethrough(true);
                    break;
                case UNDERLINED:
                    line.setUnderlined(true);
                    break;
            }
        }
        textLines.add(line);
    }

    /**
     * Builds the page into a page that can be inserted in the bookbuilder class
     *
     * @author sky
     * @return a page to be inserted in the bookbuilder class
     */
    public IChatBaseComponent build(){
        return IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(textLines.toArray(new BaseComponent[0])));
    }


    /**
     * Properties text can have
     */
    public enum Property{
        BOLD,
        ITALIC,
        OBFUSCATED,
        STRIKETHROUGH,
        UNDERLINED
    }
}
