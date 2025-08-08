package com.github.sculkhorde.core;

import com.github.sculkhorde.common.structures.SculkTombStructure;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

import java.util.Locale;


public class ModStructures {

    public static final LazyRegistrar<StructureType<?>> STRUCTURES = LazyRegistrar.create(Registries.STRUCTURE_TYPE, SculkHorde.MOD_ID);

    public static final LazyRegistrar<StructurePieceType> STRUCTURE_PIECES = LazyRegistrar.create(Registries.STRUCTURE_PIECE, SculkHorde.MOD_ID);

    public static final RegistryObject<StructureType<SculkTombStructure>> SCULK_TOMB_STRUCTURE = STRUCTURES.register("sculk_tomb_structure", () -> explicitStructureTypeTyping(SculkTombStructure.CODEC));

    /**
     * Originally, I had a double lambda ()->()-> for the RegistryObject line above, but it turns out that
     * some IDEs cannot resolve the typing correctly. This method explicitly states what the return type
     * is so that the IDE can put it into the DeferredRegistry properly.
     */
    private static <T extends Structure> StructureType<T> explicitStructureTypeTyping(Codec<T> structureCodec) {
        return () -> structureCodec;
    }

    private static RegistryObject<StructurePieceType> registerStructurePiece(String name, StructurePieceType structurePieceType) {
        return STRUCTURE_PIECES.register(name.toLowerCase(Locale.ROOT), () -> structurePieceType);
    }
}
