package com.github.alexthe666.citadel.client.gui.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ItemRenderData(
    Holder<Item> item, int x, int y, double scale, int page, DataComponentPatch components
) {
    public static final Codec<ItemRenderData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BuiltInRegistries.ITEM
                .holderByNameCodec()
                .fieldOf("item")
                .forGetter(ItemRenderData::item),

            Codec.INT
                .fieldOf("x")
                .forGetter(ItemRenderData::x),
            Codec.INT
                .fieldOf("y")
                .forGetter(ItemRenderData::y),

            Codec.DOUBLE
                .optionalFieldOf("scale", 1.0)
                .forGetter(ItemRenderData::scale),
            Codec.INT
                .optionalFieldOf("page", 0)
                .forGetter(ItemRenderData::page),

            DataComponentPatch.CODEC
                .optionalFieldOf("item_components", DataComponentPatch.EMPTY)
                .forGetter(ItemRenderData::components)
        )
            .apply(instance, ItemRenderData::new)
    );
}
