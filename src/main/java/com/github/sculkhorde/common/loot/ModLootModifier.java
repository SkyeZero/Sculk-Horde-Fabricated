package com.github.sculkhorde.common.loot;

import com.github.sculkhorde.core.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModLootModifier {

    private static final ResourceLocation ANCIENT_CITY =
            new ResourceLocation("minecraft", "chests/ancient_city");

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootDataManager, resourceLocation, builder, lootTableSource) -> {
            if (resourceLocation.equals(ANCIENT_CITY)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .conditionally(LootItemRandomChanceCondition.randomChance(0.1f).build())
                        .with(LootItem.lootTableItem(ModItems.DEEP_GREEN_MUSIC_DISC.get()).build())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 1.0f)).build());

                builder.pool(poolBuilder.build());
            }
        });
    }

}
