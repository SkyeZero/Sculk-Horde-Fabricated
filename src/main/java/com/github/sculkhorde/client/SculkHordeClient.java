package com.github.sculkhorde.client;

import com.github.sculkhorde.client.particle.BurrowedBurstParticle;
import com.github.sculkhorde.client.particle.SculkCrustParticle;
import com.github.sculkhorde.client.renderer.SculkFogRenderer;
import com.github.sculkhorde.client.renderer.block.SculkSummonerBlockRenderer;
import com.github.sculkhorde.client.renderer.block.SoulHarvesterBlockRenderer;
import com.github.sculkhorde.client.renderer.entity.*;
import com.github.sculkhorde.common.screen.SoulHarvesterScreen;
import com.github.sculkhorde.core.*;
import com.google.common.collect.Maps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Map;

public class SculkHordeClient implements ClientModInitializer {

    public final Map<EntityType<?>, EntityRenderer<?>> renderers = Maps.newHashMap();

    public <T extends Entity> void register(EntityType<T> p_229087_1_, EntityRenderer<? super T> p_229087_2_) {
        this.renderers.put(p_229087_1_, p_229087_2_);
    }

    @Override
    public void onInitializeClient() {
        registerEntityRenders();
        registerBlockRenderers();
        registerBlockEntityRenderers();
        registerParticles();
        registerMenus();
    }

    public static void registerEntityRenders() {
        EntityRendererRegistry.register(ModEntities.SCULK_ZOMBIE.get(), SculkZombieRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_MITE.get(), SculkMiteRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_MITE_AGGRESSOR.get(), SculkMiteAggressorRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SPITTER.get(), SculkSpitterRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_BEE_INFECTOR.get(), SculkBeeInfectorRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_BEE_HARVESTER.get(), SculkBeeHarvesterRenderer::new);
        EntityRendererRegistry.register(ModEntities.CUSTOM_ITEM_PROJECTILE_ENTITY.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_ACIDIC_PROJECTILE_ENTITY.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.PURIFICATION_FLASK_PROJECTILE_ENTITY.get(), ThrownItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_HATCHER.get(), SculkHatcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.CURSOR_PROBER.get(), CursorProberRenderer::new);
        EntityRendererRegistry.register(ModEntities.CURSOR_PURIFIER_PROBER.get(), CursorPurifierProberRenderer::new);
        EntityRendererRegistry.register(ModEntities.CURSOR_BRIDGER.get(), CursorBridgerRenderer::new);
        EntityRendererRegistry.register(ModEntities.CURSOR_SURFACE_INFECTOR.get(), CursorSurfaceInfectorRenderer::new);
        EntityRendererRegistry.register(ModEntities.CURSOR_SURFACE_PURIFIER.get(), CursorSurfacePurifierRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SPORE_SPEWER.get(), SculkSporeSpewerRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_RAVAGER.get(), SculkRavagerRenderer::new);
        EntityRendererRegistry.register(ModEntities.INFESTATION_PURIFIER.get(), InfestationPurifierRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_VINDICATOR.get(), SculkVindicatorRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_CREEPER.get(), SculkCreeperRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_ENDERMAN.get(), SculkEndermanRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_PHANTOM.get(), SculkPhantomRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_PHANTOM_CORPSE.get(), SculkPhantomCorpseRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SALMON.get(), SculkSalmonRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SQUID.get(), SculkSquidRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_PUFFERFISH.get(), SculkPufferfishRenderer::new);
        EntityRendererRegistry.register(ModEntities.ENDER_BUBBLE_ATTACK.get(), EnderBubbleAttackRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAOS_TELEPORATION_RIFT.get(), ChaosTeleporationRiftRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SPINE_SPIKE_ATTACK.get(), SculkSpineSpikeAttackRenderer::new);
        EntityRendererRegistry.register(ModEntities.AREA_EFFECT_SPHERICAL_CLOUD.get(), AreaEffectSphericalCloudRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_WITCH.get(), SculkWitchRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SOUL_REAPER.get(), SculkSoulReaperRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_VEX.get(), SculkVexRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_WITCH.get(), SculkWitchRenderer::new);
        EntityRendererRegistry.register(ModEntities.LIVING_ARMOR.get(), LivingArmorRenderer::new);
        EntityRendererRegistry.register(ModEntities.GOLEM_OF_WRATH.get(), GolemOfWrathRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_GUARDIAN.get(), SculkGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_BROOD_HATCHER.get(), SculkBroodHatcherRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_BROODLING.get(), SculkBroodlingRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_SHEEP.get(), SculkSheepRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_METAMORPHOSIS_POD.get(), SculkMetamorphosisPodRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_GHAST.get(), SculkGhastRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_LEECH.get(), SculkLeechRenderer::new);
        EntityRendererRegistry.register(ModEntities.SCULK_STINGER.get(), SculkStingerRenderer::new);

        EntityRendererRegistry.register(ModEntities.SOUL_FIRE_PROJECTILE.get(), SoulFireProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_POISON_PROJECTILE.get(), SoulPoisonProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_ICE_PROJECTILE.get(), SoulIceProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_BREEZE_PROJECTILE.get(), SoulBreezeProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_SPEAR_PROJECTILE.get(), SoulSpearProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_FLY_SWATTER_PROJECTILE.get(), SoulFlySwatterProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.FLOOR_SOUL_SPEARS.get(), FloorSoulSpearsRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEMENTAL_FIRE_MAGIC_CIRCLE.get(), ElementalFireMagicCircleRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEMENTAL_BREEZE_MAGIC_CIRCLE.get(), ElementalBreezeMagicCircleRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEMENTAL_POISON_MAGIC_CIRCLE.get(), ElementalPoisonMagicCircleRenderer::new);
        EntityRendererRegistry.register(ModEntities.ELEMENTAL_ICE_MAGIC_CIRCLE.get(), ElementalIceMagicCircleRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_SPEAR_SUMMONER.get(), SoulSpearSummonerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ZOLTRAAK_ATTACK_ENTITY.get(), ZoltraakAttackRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOUL_BLAST_ATTACK_ENTITY.get(), SoulBlastAttackEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ACID_BLOB_PROJECTILE_ENTITY.get(), AcidBlobProjectileRenderer::new);
    }

    protected static void registerBlockRenderers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPIKE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SCULK_SHROOM_CULTURE.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMALL_SHROOM.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRASS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GRASS_SHORT.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TENDRILS.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DISEASED_KELP_BLOCK.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOULITE_BUD_BLOCK.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOULITE_CLUSTER_BLOCK.get(), RenderType.cutout());
    }

    protected static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlockEntities.SCULK_SUMMONER_BLOCK_ENTITY.get(), context -> new SculkSummonerBlockRenderer());
        BlockEntityRenderers.register(ModBlockEntities.SOUL_HARVESTER_BLOCK_ENTITY.get(), context -> new SoulHarvesterBlockRenderer());
    }

    public static void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(ModParticles.SCULK_CRUST_PARTICLE.get(), SculkCrustParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BURROWED_BURST_PARTICLE.get(), BurrowedBurstParticle.Factory::new);
    }

    public static void registerMenus() {
        MenuScreens.register(ModMenuTypes.SOUL_HARVESTER_MENU, SoulHarvesterScreen::new);
    }
}
