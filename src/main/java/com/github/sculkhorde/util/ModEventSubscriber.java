package com.github.sculkhorde.util;

import com.github.sculkhorde.common.advancement.*;
import com.github.sculkhorde.common.entity.*;
import com.github.sculkhorde.common.entity.boss.sculk_enderman.SculkEndermanEntity;
import com.github.sculkhorde.common.entity.boss.sculk_soul_reaper.LivingArmorEntity;
import com.github.sculkhorde.common.entity.boss.sculk_soul_reaper.SculkSoulReaperEntity;
import com.github.sculkhorde.core.ModEntities;
import com.github.sculkhorde.core.ModPotions;
import com.github.sculkhorde.core.SculkHorde;
import com.github.sculkhorde.systems.infestation_systems.block_infestation_system.BlockInfestationSystem;
import com.github.sculkhorde.systems.gravemind_system.entity_factory.EntityFactory;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEventSubscriber {

    /*
    public static void onCommonSetup(FMLCommonSetupEvent event)
    {
        EntityFactory.initialize();
        BlockInfestationSystem.initialize();

        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.SCULK_MITE.get(),
                    SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                    SculkMiteEntity::additionalSpawnCheck);
            afterCommonSetup();
            ModPotions.registerRecipes();
        });


    }

    // runs on main thread after common setup event
    // adding things to unsynchronized registries (i.e. most vanilla registries) can be done here
    private static void afterCommonSetup()
    {
        CriteriaTriggers.register(GravemindEvolveImmatureTrigger.INSTANCE);
        CriteriaTriggers.register(GravemindEvolveMatureTrigger.INSTANCE);
        CriteriaTriggers.register(SculkHordeStartTrigger.INSTANCE);
        CriteriaTriggers.register(SculkNodeSpawnTrigger.INSTANCE);
        CriteriaTriggers.register(SoulHarvesterTrigger.INSTANCE);
        CriteriaTriggers.register(SculkHordeDefeatTrigger.INSTANCE);
        CriteriaTriggers.register(ContributeTrigger.INSTANCE);
    }

    public static void entityAttributes(EntityAttributeCreationEvent event) {
    }
     */
}

