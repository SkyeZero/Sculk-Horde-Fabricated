package com.github.sculkhorde.core;

import com.github.sculkhorde.util.TickUnits;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, SculkHorde.MOD_ID);

    public static final RegistryObject<Potion> CORRODED_POTION = POTIONS.register("corroded_potion", () -> new Potion(new MobEffectInstance(ModMobEffects.CORRODED.get(), TickUnits.convertMinutesToTicks(6), 0)));

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}
