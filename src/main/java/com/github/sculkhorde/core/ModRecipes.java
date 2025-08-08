package com.github.sculkhorde.core;

import com.github.sculkhorde.common.recipe.SoulHarvestingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class ModRecipes {
    public static final LazyRegistrar<RecipeSerializer<?>> SERIALIZERS = LazyRegistrar.create(Registries.RECIPE_SERIALIZER, SculkHorde.MOD_ID);

    public static final RegistryObject<RecipeSerializer<?>> SOUL_HARVESTING = SERIALIZERS.register("soul_harvesting", () -> SoulHarvestingRecipe.Serializer.INSTANCE);

    public static void register() {
        SERIALIZERS.register();
    }
}
