package com.github.sculkhorde.util;

import com.github.sculkhorde.common.advancement.ContributeTrigger;
import com.github.sculkhorde.common.block.FleshyCompostBlock;
import com.github.sculkhorde.core.*;
import com.github.sculkhorde.fabricated.MobTargetSelector;
import com.github.sculkhorde.fabricated.events.EntityEvents;
import com.github.sculkhorde.fabricated.events.PlayerEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;

import java.util.function.Predicate;


public class FabricEventSubscriber {

    public static void register() {
        ServerTickEvents.START_WORLD_TICK.register(FabricEventSubscriber::onWorldStartTick);
        ServerTickEvents.START_SERVER_TICK.register(FabricEventSubscriber::onServerStartTick);
        ServerLivingEntityEvents.AFTER_DEATH.register(FabricEventSubscriber::afterLivingEntityDeath);

        EntityEvents.EFFECT_EXPIRED.register(FabricEventSubscriber::onEffectExpired);
        EntityEvents.ENTITY_ADDED.register(FabricEventSubscriber::onEntityJoinLevelEvent);
        EntityEvents.LIVING_DAMAGE.register(FabricEventSubscriber::onLivingDamaged);

        PlayerEvents.PLAYER_END_TICK.register(FabricEventSubscriber::onPlayerTick);
        PlayerEvents.PLAYER_LOGIN.register(FabricEventSubscriber::onPlayerLogIn);
        PlayerEvents.PLAYER_RESPAWN.register(FabricEventSubscriber::onPlayerRespawn);
    }

    /**
     * Gets Called Every tick when a world is running.
     * @param level - Current level being ticked
     */
    public static void onWorldStartTick(ServerLevel level)
    {
        // If we are on client, or we are not in the overworld, return
        if(level.isClientSide() || !level.equals(ServerLifecycleHooks.getCurrentServer().overworld())) return;

        if(SculkHorde.gravemind == null) {
            ModSavedData.initializeData();
            return;
        }

        if(!SculkHorde.gravemind.isWorldFullyLoaded) {
            SculkHorde.gravemind.isWorldFullyLoaded = true;
        }

        SculkHorde.gravemind.serverTick();
    }


    public static void onServerStartTick(MinecraftServer server) {
        // If we are on client, or we are not in the overworld, return
        if(SculkHorde.gravemind == null) {
            //ModSavedData.InitializeData();
        }
    }
    
    
    public static void afterLivingEntityDeath(LivingEntity theDeadOne, DamageSource theOneWhoMadeThemDead) {
        if(theDeadOne.level().isClientSide()) {
            return;
        }

        if(EntityAlgorithms.isSculkLivingEntity.test(theDeadOne))
        {
            ModSavedData.getSaveData().reportDeath((ServerLevel) theDeadOne.level(), theDeadOne.blockPosition());
            ModSavedData.getSaveData().addHostileToMemory(theDeadOne.getLastHurtByMob());
            SculkHorde.statisticsData.incrementTotalUnitDeaths();
            SculkHorde.statisticsData.addTotalMassRemovedFromHorde((int) theDeadOne.getMaxHealth());
            return;

        }

        Entity killerEntity = theOneWhoMadeThemDead.getEntity();
        if(killerEntity instanceof LivingEntity killerLivingEntity)
        {
            if(EntityAlgorithms.isSculkLivingEntity.test(killerLivingEntity))
            {
                FleshyCompostBlock.placeBlock(theDeadOne);
            }
        }

        // If a player kills an entity (That is not sculk)
        if(killerEntity instanceof ServerPlayer player)
        {
            if(EntityAlgorithms.isSculkLivingEntity.test(theDeadOne))
            {
                return;
            }

            InventoryUtil.repairIHealthRepairableItemStacks(player.getInventory(), (int) theDeadOne.getMaxHealth());
        }

    }

    
    public static void onEffectExpired(LivingEntity entity, MobEffectInstance effect) {
        if(entity.level().isClientSide() || SculkHorde.gravemind == null) return;
        if(effect.getEffect() instanceof EntityEvents.MobEffectExpireEvent event) {
            event.onExpired(entity, effect);
        }
    }

    public static void onLivingDamaged(EntityEvents.LivingDamageEvent event) {
        // Get Item being used to attack
        ItemStack itemStack = ItemStack.EMPTY;
        Entity damageSourceEntity = event.getSource().getEntity();
        LivingEntity targetEntity = event.getEntity();

        if(EntityAlgorithms.isSculkLivingEntity.test(targetEntity) && damageSourceEntity instanceof LivingEntity)
        {
            ModSavedData.getSaveData().addHostileToMemory((LivingEntity) damageSourceEntity);
        }


        if(damageSourceEntity instanceof LivingEntity attackingEntity)
        {
            itemStack = attackingEntity.getMainHandItem();
            if(!itemStack.getItem().equals(ModItems.SCULK_SWEEPER_SWORD.get()))
            {
               return;
            }

            if(EntityAlgorithms.isSculkLivingEntity.test(targetEntity))
            {
                itemStack.setDamageValue((int) Math.max(0, itemStack.getDamageValue() - event.getDamageValue()));
            }
        }
    }

    
    public static void onPlayerTick(Player player) {
        if(player.level().isClientSide()) return;

        if(player.tickCount % 20 == 0) {
            AdvancementUtil.advancementHandlingTick((ServerLevel) player.level());
        }
    }

    
    public static void onPlayerLogIn(ServerPlayer player) {
        if(SculkHorde.contributionHandler.isContributor(player) && !SculkHorde.contributionHandler.doesPlayerHaveContributionAdvancement(player))
        {
            AdvancementUtil.giveAdvancementToPlayer(player, ContributeTrigger.INSTANCE);
            SculkHorde.contributionHandler.givePlayerCoinOfContribution(player);
        }
    }

    
    public static void onPlayerRespawn(ServerPlayer player) {
        if(PlayerProfileHandler.isPlayerActiveVessel(player))
        {
            MobEffectInstance effectInstance = new MobEffectInstance(ModMobEffects.SCULK_VESSEL.get(), Integer.MAX_VALUE);
            player.addEffect(effectInstance);
        }
    }

    
    public static void onEntityJoinLevelEvent(Entity entity)
    {
        if(entity.level().isClientSide())
        {
            return;
        }

        if(entity instanceof Mob mob) {
            if(!EntityAlgorithms.isLivingEntityExplicitDenyTarget(mob) && mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE))
            {
                ((MobTargetSelector) mob).getTargetSelector().addGoal(0, new NearestAttackableTargetGoal<>(mob, LivingEntity.class, true, shouldEntitiesAttackTheSculkHorde));
            }
        }

        if(entity instanceof Animal animal)
        {
            if(!EntityAlgorithms.isSculkLivingEntity.test(animal) && !EntityAlgorithms.isLivingEntityAllyToSculkHorde(animal))
            {
                ((MobTargetSelector) animal).getTargetSelector().addGoal(0, new AvoidEntityGoal<LivingEntity>(animal, LivingEntity.class, 6.0F, 1.0F, 1.2F, shouldEntitiesAvoidTheSculkHorde));
            }
        }

        if(entity instanceof Villager villager)
        {
            if(!EntityAlgorithms.isSculkLivingEntity.test(villager) && !EntityAlgorithms.isLivingEntityAllyToSculkHorde(villager))
            {
                ((MobTargetSelector) villager).getTargetSelector().addGoal(0, new AvoidEntityGoal<LivingEntity>(villager, LivingEntity.class, 6.0F, 1.0F, 1.2F, shouldEntitiesAvoidTheSculkHorde));
            }
        }

    }

    public static Predicate<LivingEntity> shouldEntitiesAttackTheSculkHorde = (e) ->
    {
        return ModConfig.SERVER.should_all_other_mobs_attack_the_sculk_horde.get() && EntityAlgorithms.isSculkLivingEntity.test(e);
    };

    public static Predicate<LivingEntity> shouldEntitiesAvoidTheSculkHorde = (e) ->
    {
        return ModConfig.SERVER.should_animals_and_villagers_avoid_the_sculk_horde.get() && EntityAlgorithms.isSculkLivingEntity.test(e);
    };
}
