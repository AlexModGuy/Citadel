package com.github.alexthe666.citadel.server.block;

import com.github.alexthe666.citadel.Citadel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class LecternBooks {

    public static Map<ResourceLocation, BookData> BOOKS = new HashMap<>();

    public static void init(){
        BOOKS.put(Citadel.CITADEL_BOOK.getId(), new BookData(0X64A27B, 0XD6D6D6));
    }

    public static boolean isLecternBook(ItemStack stack){
        return BOOKS.containsKey(ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }

    public static class BookData{
        int bindingColor;
        int pageColor;

        public BookData(int bindingColor, int pageColor) {
            this.bindingColor = bindingColor;
            this.pageColor = pageColor;
        }

        public int getBindingColor() {
            return bindingColor;
        }

        public int getPageColor() {
            return pageColor;
        }
    }
}
