package com.github.sculkhorde.core;

import com.github.sculkhorde.fabricated.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.core.registries.Registries;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;

public class ModParticles {
    public static final LazyRegistrar<ParticleType<?>> PARTICLE_TYPES =  LazyRegistrar.create(Registries.PARTICLE_TYPE, SculkHorde.MOD_ID);

    public static final RegistryObject<SimpleParticleType> SCULK_CRUST_PARTICLE = PARTICLE_TYPES.register("sculk_crust_particle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BURROWED_BURST_PARTICLE = PARTICLE_TYPES.register("burrowed_burst_particle", () -> new SimpleParticleType(false));

}
