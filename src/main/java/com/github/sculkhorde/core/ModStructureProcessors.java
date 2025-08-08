package com.github.sculkhorde.core;

import com.github.sculkhorde.common.world.processors.WaterloggingFixProcessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class ModStructureProcessors {

    public static final LazyRegistrar<StructureProcessorType<?>> PROCESSORS = LazyRegistrar.create(Registries.STRUCTURE_PROCESSOR, SculkHorde.MOD_ID);

    public static final RegistryObject<StructureProcessorType<WaterloggingFixProcessor>> WATERLOGGING_FIX_PROCESSOR = PROCESSORS.register("waterlogging_fix_processor", () -> () ->WaterloggingFixProcessor.CODEC);
}
