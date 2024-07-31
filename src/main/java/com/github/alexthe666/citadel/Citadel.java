package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.config.ConfigHolder;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemCitadelBook;
import com.github.alexthe666.citadel.item.ItemCitadelDebug;
import com.github.alexthe666.citadel.item.ItemCustomRender;
import com.github.alexthe666.citadel.server.CitadelEvents;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlock;
import com.github.alexthe666.citadel.server.block.CitadelLecternBlockEntity;
import com.github.alexthe666.citadel.server.block.LecternBooks;
import com.github.alexthe666.citadel.server.generation.SpawnProbabilityModifier;
import com.github.alexthe666.citadel.server.generation.VillageHouseManager;
import com.github.alexthe666.citadel.server.world.ExpandedBiomeSource;
import com.github.alexthe666.citadel.server.world.ExpandedBiomes;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

;

@Mod("citadel")
public class Citadel {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    public static List<String> PATREONS = new ArrayList<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "citadel");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "citadel");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "citadel");

    public static final Supplier<Item> DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Properties()));
    public static final Supplier<Item> CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final Supplier<Block> LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)));

    public static final Supplier<BlockEntityType<CitadelLecternBlockEntity>> LECTERN_BE = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN.get()).build(null));


    public Citadel(ModContainer container, IEventBus bus) {
        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::onModConfigEvent);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        final DeferredRegister<Codec<? extends BiomeModifier>> serializers = DeferredRegister.create((ResourceKey<? extends Registry<Codec<? extends BiomeModifier>>>) Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, "citadel");
        serializers.register(bus);
        serializers.register("mob_spawn_probability", SpawnProbabilityModifier::makeCodec);
        NeoForge.EVENT_BUS.register(this);
        container.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        NeoForge.EVENT_BUS.register(new CitadelEvents());
    }


    private void setup(final FMLCommonSetupEvent event) {
        LecternBooks.init();
        int packetsRegistered = 0;
        BufferedReader urlContents = WebHelper.getURLContents("https://raw.githubusercontent.com/Alex-the-666/Citadel/master/src/main/resources/assets/citadel/patreon.txt", "assets/citadel/patreon.txt");
        if (urlContents != null) {
            try {
                String line;
                while ((line = urlContents.readLine()) != null) {
                    PATREONS.add(line);
                }
            } catch (IOException e) {
                LOGGER.warn("Failed to load patreon contributor perks");
            }
        } else LOGGER.warn("Failed to load patreon contributor perks");
    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings.get();
        if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker.get();
            ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier.get();
            ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent.get();
            //citadelTestBiomeData = SpawnBiomeConfig.create(new ResourceLocation("citadel:config_biome"), CitadelBiomeDefinitions.TERRALITH_TEST);
        }
    }



    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.onClientInit());
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        RegistryAccess registryAccess = event.getServer().registryAccess();
        VillageHouseManager.addAllHouses(registryAccess);
        Registry<Biome> allBiomes = registryAccess.registryOrThrow(Registries.BIOME);
        Registry<LevelStem> levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
        Map<ResourceKey<Biome>, Holder<Biome>> biomeMap = new HashMap<>();
        for(ResourceKey<Biome> biomeResourceKey : allBiomes.registryKeySet()){
            Optional<Holder.Reference<Biome>> holderOptional = allBiomes.getHolder(biomeResourceKey);
            holderOptional.ifPresent(biomeHolder -> biomeMap.put(biomeResourceKey, biomeHolder));
        }
        for (ResourceKey<LevelStem> levelStemResourceKey : levelStems.registryKeySet()) {
            Optional<Holder.Reference<LevelStem>> holderOptional = levelStems.getHolder(levelStemResourceKey);
            if(holderOptional.isPresent() && holderOptional.get().value().generator().getBiomeSource() instanceof ExpandedBiomeSource expandedBiomeSource){
                expandedBiomeSource.setResourceKeyMap(biomeMap);
                Set<Holder<Biome>> biomeHolders = ExpandedBiomes.buildBiomeList(registryAccess, levelStemResourceKey);
                expandedBiomeSource.expandBiomesWith(biomeHolders);
            }
        }
    }
    
}
