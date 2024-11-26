package com.github.alexthe666.citadel.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    public final ModConfigSpec.BooleanValue citadelEntityTracker;
    public final ModConfigSpec.BooleanValue skipDatapackWarnings;
    public final ModConfigSpec.DoubleValue chunkGenSpawnModifier;
    public final ModConfigSpec.BooleanValue aprilFoolsContent;
    public static boolean citadelEntityTrack;
    public static boolean skipWarnings;
    public static double chunkGenSpawnModifierVal = 1.0D;
    public static boolean aprilFools;

    public ServerConfig(final ModConfigSpec.Builder builder) {
        builder.push("general");
        this.citadelEntityTracker = buildBoolean(builder, "Track Entities", "all", true, "True if citadel tracks entity properties(freezing, stone mobs, etc) on server. Turn this to false to solve some server lag, may break some stuff.");
        this.skipDatapackWarnings = buildBoolean(builder, "Skip Datapack Warnings", "all", true, "True to skip warnings about using datapacks.");
        this.chunkGenSpawnModifier = builder.comment("Multiplies the count of entities spawned by this number. 0 = no entites added on chunk gen, 2 = twice as many entities added on chunk gen. Useful for many mods that add a lot of creatures, namely animals, to the spawn lists.").translation("chunkGenSpawnModifier").defineInRange("chunkGenSpawnModifier", 1.0F, 0.0F, 100000F);
        this.aprilFoolsContent = buildBoolean(builder, "April Fools Content", "all", true, "True to if april fools content can display on april fools.");
    }

    private static ModConfigSpec.BooleanValue buildBoolean(ModConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }
}
