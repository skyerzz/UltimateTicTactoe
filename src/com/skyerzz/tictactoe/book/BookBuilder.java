package com.skyerzz.tictactoe.book;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaBook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

/**
 * Book Builder class. Creates a book based on pages added.
 * Created by sky on 2-11-2017.
 */
public class BookBuilder {

    private List<IChatBaseComponent> pages;
    private BookMeta meta;
    private ItemStack book;

    /**
     * Creates a new bookBuilder
     *
     * @author Sky
     * @param title title of the book
     * @param author author of the book
     */
    public BookBuilder(String title, String author){
        book = new ItemStack(Material.WRITTEN_BOOK);
        meta = (BookMeta) book.getItemMeta();
        meta.setTitle(title);
        meta.setAuthor(author);

        try {
            pages = (List<IChatBaseComponent>) CraftMetaBook.class.getDeclaredField("pages").get(meta);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds and finalizes the book
     *
     * @author Sky
     * @return The book as ItemStack
     */
    public ItemStack build(){
        book.setItemMeta(meta);
        return book;
    }

    /**
     * Adds a page to the book
     *
     * @author Sky
     * @param page a Page to add to the book
     */
    public void addPage(Page page){
        pages.add(page.build());
    }
}
