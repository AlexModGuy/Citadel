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
import com.github.alexthe666.citadel.server.message.*;
import com.github.alexthe666.citadel.web.WebHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod("citadel")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class Citadel {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static ServerProxy PROXY = unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static List<String> PATREONS = new ArrayList<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, "citadel");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, "citadel");
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "citadel");

    public static final DeferredHolder<Item, ItemCitadelDebug> DEBUG_ITEM = ITEMS.register("debug", () -> new ItemCitadelDebug(new Item.Properties()));
    public static final DeferredHolder<Item, ItemCitadelBook> CITADEL_BOOK = ITEMS.register("citadel_book", () -> new ItemCitadelBook(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ItemCustomRender> EFFECT_ITEM = ITEMS.register("effect_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ItemCustomRender> FANCY_ITEM = ITEMS.register("fancy_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, ItemCustomRender> ICON_ITEM = ITEMS.register("icon_item", () -> new ItemCustomRender(new Item.Properties().stacksTo(1)));

    public static final Supplier<Block> LECTERN = BLOCKS.register("lectern", () -> new CitadelLecternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)));

    public static final Supplier<BlockEntityType<CitadelLecternBlockEntity>> LECTERN_BE = BLOCK_ENTITIES.register("lectern", () -> BlockEntityType.Builder.of(CitadelLecternBlockEntity::new, LECTERN.get()).build(null));

    public Citadel(ModContainer modContainer, IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        final DeferredRegister<MapCodec<? extends BiomeModifier>> serializers = DeferredRegister.create(NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, "citadel");
        serializers.register(bus);
        serializers.register("mob_spawn_probability", SpawnProbabilityModifier::makeCodec);
        // Only register ClientProxy to event bus - ServerProxy has no @SubscribeEvent methods
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.register(PROXY);
        }
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        NeoForge.EVENT_BUS.register(new CitadelEvents());
        // Register NeoForge bus events (non-mod lifecycle events)
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, Citadel::onServerAboutToStart);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PROXY.onPreInit();
            LecternBooks.init();
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
        });
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent.Reloading event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        ServerConfig.skipWarnings = ConfigHolder.SERVER.skipDatapackWarnings.get();
        if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
            ServerConfig.citadelEntityTrack = ConfigHolder.SERVER.citadelEntityTracker.get();
            ServerConfig.chunkGenSpawnModifierVal = ConfigHolder.SERVER.chunkGenSpawnModifier.get();
            ServerConfig.aprilFools = ConfigHolder.SERVER.aprilFoolsContent.get();
            //citadelTestBiomeData = SpawnBiomeConfig.create(ResourceLocation.parse("citadel:config_biome"), CitadelBiomeDefinitions.TERRALITH_TEST);
        }
    }

    @SubscribeEvent
    public static void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.onClientInit());
    }

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("citadel").versioned("2.7.0").optional();
        registrar.playToServer(PropertiesMessage.TYPE, PropertiesMessage.CODEC, PropertiesMessage::handle);
        registrar.playToServer(AnimationMessage.TYPE, AnimationMessage.CODEC, AnimationMessage::handle);
        registrar.playToServer(DanceJukeboxMessage.TYPE, DanceJukeboxMessage.CODEC, DanceJukeboxMessage::handle);
        registrar.playToServer(SyncePathMessage.TYPE, SyncePathMessage.CODEC, SyncePathMessage::handle);
        registrar.playToServer(SyncPathReachedMessage.TYPE, SyncPathReachedMessage.CODEC, SyncPathReachedMessage::handle);
    }

    // Registered manually to NeoForge.EVENT_BUS in constructor (not a mod bus event)
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        RegistryAccess registryAccess = event.getServer().registryAccess();
        VillageHouseManager.addAllHouses(registryAccess);
    }

    private static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }
}