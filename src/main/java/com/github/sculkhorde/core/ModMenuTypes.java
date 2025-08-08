package com.github.sculkhorde.core;

import com.github.sculkhorde.common.screen.SoulHarvesterMenu;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {

    public static final MenuType<SoulHarvesterMenu> SOUL_HARVESTER_MENU =
            Registry.register(BuiltInRegistries.MENU, new ResourceLocation(SculkHorde.MOD_ID, "soul_harvester_menu"), new ExtendedScreenHandlerType<>(SoulHarvesterMenu::new));

}
