package com.github.sculkhorde.core;

import com.github.sculkhorde.common.loot.ModLootModifier;
import com.github.sculkhorde.common.pools.PoolBlocks;
import com.github.sculkhorde.misc.StatisticsData;
import com.github.sculkhorde.misc.contributions.ContributionHandler;
import com.github.sculkhorde.systems.*;
import com.github.sculkhorde.systems.chunk_cursor_system.ChunkInfestationSystem;
import com.github.sculkhorde.systems.cursor_system.CursorSystem;
import com.github.sculkhorde.systems.event_system.EventSystem;
import com.github.sculkhorde.systems.event_system.events.HitSquadEvent.HitSquadDispatcherSystem;
import com.github.sculkhorde.systems.gravemind_system.Gravemind;
import com.github.sculkhorde.systems.gravemind_system.entity_factory.EntityFactory;
import com.github.sculkhorde.systems.infestation_systems.block_infestation_system.BlockInfestationSystem;
import com.github.sculkhorde.systems.path_builder_system.PathBuilderSystem;
import com.github.sculkhorde.systems.raid_system.RaidHandler;
import com.github.sculkhorde.util.ChunkLoading.BlockEntityChunkLoaderHelper;
import com.github.sculkhorde.util.ChunkLoading.EntityChunkLoaderHelper;
import com.github.sculkhorde.util.DeathAreaInvestigator;
import com.github.sculkhorde.util.FabricEventSubscriber;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;
//HOW TO EXPORT MOD: https://www.youtube.com/watch?v=x3wKsiQ37Wc

//The @Mod tag is here to let the compiler know that this is our main mod class
//It takes in our mod id so it knows what mod it is loading.
public class SculkHorde implements ModInitializer {

    //Here I've created a variable of our mod id so we can use it throughout our project
    public static final String MOD_ID = "sculkhorde";
    //The file name in the world data folder.
    public static final String SAVE_DATA_ID = SculkHorde.MOD_ID + "_gravemind_memory";
    //The Creative Tab that all the items appear in
    private static boolean DEBUG_MODE = false;
    public static EntityFactory entityFactory = new EntityFactory();
    public static Gravemind gravemind;
    public static DebugSlimeSystem debugSlimeSystem;
    public static final Logger LOGGER = LogUtils.getLogger();
    public static PoolBlocks randomSculkFlora;
    public static DeathAreaInvestigator deathAreaInvestigator;
    public static RaidHandler raidHandler;
    public static EventSystem eventSystem;
    public static CursorSystem cursorSystem = new CursorSystem();
    public static SculkNodesSystem sculkNodesSystem;
    public static StatisticsData statisticsData;
    public static BlockEntityChunkLoaderHelper blockEntityChunkLoaderHelper;
    public static EntityChunkLoaderHelper entityChunkLoaderHelper = new EntityChunkLoaderHelper();
    public static final ContributionHandler contributionHandler = new ContributionHandler();
    public static final SculkPopulationSystem populationHandler = new SculkPopulationSystem();
    public static final HitSquadDispatcherSystem hitSquadDispatcherSystem = new HitSquadDispatcherSystem();
    public static BeeNestActivitySystem beeNestActivitySystem;

    public static AutoPerformanceSystem autoPerformanceSystem;
    public static ChunkInfestationSystem chunkInfestationSystem;
    public static PathBuilderSystem pathBuilderSystem;

    //This is the instance of our class, and we register it to the ModEventBus (which I have stored in a variable).
    @Override
    public void onInitialize() {
        ModConfig.loadConfig(ModConfig.SERVER_SPEC, FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + "_config.toml").toString());

        GeckoLib.initialize();
        ModBlocks.BLOCKS.register(); //Load Blocks
        ModItems.ITEMS.register(); //Load Items
        ModBlockEntities.register(); //Load Tile Entities
        ModEntities.register(); //Load Entities (this may not be necessary anymore)
        // bus.register(ModEntities.class); //Load Entities
        ModStructures.STRUCTURES.register(); //Load Structures
        ModStructures.STRUCTURE_PIECES.register(); //Load Structure Pieces
        ModStructureProcessors.PROCESSORS.register(); //Load Processors
        ModCommands.init();
        ModPotions.register(); //Load Potions
        // ModMenuTypes.register(); //Load Menus
        ModMobEffects.EFFECTS.register(); //Load Effects
        ModParticles.PARTICLE_TYPES.register(); //Load Particles
        ModSounds.SOUND_EVENTS.register(); //Load Sounds
        ModCreativeModeTab.TABS.register(); //Load Creative Tabs
        ModRecipes.register(); //Load Recipes
        ModLootModifier.register();
        ModAdvancements.init();

        BlockInfestationSystem.initialize();
        FabricEventSubscriber.register();
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static void setDebugMode(boolean debugMode) {
        DEBUG_MODE = debugMode;
    }

}