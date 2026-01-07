package com.github.alexthe666.citadel.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record CustomRenderDisplay(
    ItemStack item,
    boolean shake,
    boolean bob,
    boolean spin,
    boolean zoom,
    float scale
) {
    public static final CustomRenderDisplay DEFAULT = new CustomRenderDisplay(new ItemStack(Items.BARRIER), false, false, false, false, 1f);

    public static final Codec<CustomRenderDisplay> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.withAlternative(ItemStack.CODEC, Codec.unit(new ItemStack(Items.BARRIER)))
                .fieldOf("item")
                .forGetter(CustomRenderDisplay::item),
            Codec.BOOL
                .optionalFieldOf("shake", false)
                .forGetter(CustomRenderDisplay::shake),
            Codec.BOOL
                .optionalFieldOf("bob", false)
                .forGetter(CustomRenderDisplay::bob),
            Codec.BOOL
                .optionalFieldOf("spin", false)
                .forGetter(CustomRenderDisplay::spin),
            Codec.BOOL
                .optionalFieldOf("zoom", false)
                .forGetter(CustomRenderDisplay::zoom),
            Codec.FLOAT
                .optionalFieldOf("scale", 1f)
                .forGetter(CustomRenderDisplay::scale)
        )
            .apply(instance, CustomRenderDisplay::new)
    );
}
