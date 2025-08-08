package com.github.sculkhorde.core;

import com.github.sculkhorde.util.TickUnits;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class ModPotions {
    public static final LazyRegistrar<Potion> POTIONS = LazyRegistrar.create(Registries.POTION, SculkHorde.MOD_ID);

    public static final RegistryObject<Potion> CORRODED_POTION = POTIONS.register("corroded_potion", () -> new Potion(new MobEffectInstance(ModMobEffects.CORRODED.get(), TickUnits.convertMinutesToTicks(6), 0)));

    public static void register() {
        POTIONS.register();
    }

    public static void registerRecipes()
    {
        /*
        BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.THICK)), Ingredient.of(ModItems.SCULK_ACIDIC_PROJECTILE.get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), CORRODED_POTION.get()));
         */
        FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.THICK, Ingredient.of(ModItems.SCULK_ACIDIC_PROJECTILE.get()), CORRODED_POTION.get());
    }
}
