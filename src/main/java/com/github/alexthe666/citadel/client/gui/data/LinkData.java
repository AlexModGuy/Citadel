package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record LinkData(
    String linkedPage, Component titleText,
    int x, int y, int page,
    ItemStack displayItem
) {
    public static final Codec<LinkData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING
                .fieldOf("linked_page")
                .forGetter(LinkData::linkedPage),
            ComponentSerialization.CODEC
                .fieldOf("text")
                .forGetter(LinkData::titleText),
            Codec.INT
                .fieldOf("x")
                .forGetter(LinkData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(LinkData::y),
            Codec.INT
                .fieldOf("page")
                .forGetter(LinkData::page),
            ItemStack.OPTIONAL_CODEC
                .optionalFieldOf("stack", ItemStack.EMPTY)
                .forGetter(LinkData::displayItem)
        )
            .apply(instance, LinkData::new)
    );
}
