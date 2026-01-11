package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.*;
import com.google.gson.*;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.*;

public record BookPage(
    Optional<String> parent, String textFileToReadFrom,

    List<LinkData> linkedButtons, List<EntityLinkData> linkedEntities,
    List<ItemRenderData> itemRenders, List<RecipeData> recipes,
    List<TabulaRenderData> tabulaRenders, List<EntityRenderData> entityRenders,
    List<ImageData> images,

    Component title
) {
    public static final Codec<BookPage> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING
                .optionalFieldOf("parent")
                .forGetter(BookPage::parent),
            Codec.STRING
                .fieldOf("text")
                .forGetter(BookPage::textFileToReadFrom),

            LinkData.CODEC.listOf()
                .optionalFieldOf("linked_page_buttons", Collections.emptyList())
                .forGetter(BookPage::linkedButtons),
            EntityLinkData.CODEC.listOf()
                .optionalFieldOf("entity_buttons", Collections.emptyList())
                .forGetter(BookPage::linkedEntities),
            ItemRenderData.CODEC.listOf()
                .optionalFieldOf("item_renders", Collections.emptyList())
                .forGetter(BookPage::itemRenders),
            RecipeData.CODEC.listOf()
                .optionalFieldOf("recipes", Collections.emptyList())
                .forGetter(BookPage::recipes),
            TabulaRenderData.CODEC.listOf()
                .optionalFieldOf("tabula_renders", Collections.emptyList())
                .forGetter(BookPage::tabulaRenders),
            EntityRenderData.CODEC.listOf()
                .optionalFieldOf("entity_renders", Collections.emptyList())
                .forGetter(BookPage::entityRenders),
            ImageData.CODEC.listOf()
                .optionalFieldOf("images", Collections.emptyList())
                .forGetter(BookPage::images),

            Codec.either(
                    Codec.STRING.xmap(Component::translatable, c -> c.getString()),
                    ComponentSerialization.CODEC
                )
                .xmap(Either::unwrap, Either::right)
                .optionalFieldOf("title", Component.empty())
                .forGetter(BookPage::title)
        )
            .apply(instance, BookPage::new)
    );
}
