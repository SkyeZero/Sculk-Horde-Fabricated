package com.github.sculkhorde.mixin.fabricated;

import com.github.sculkhorde.fabricated.events.EntityEvents;
import com.github.sculkhorde.fabricated.events.PlayerEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class ServerLevelEventsMixin {

    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;addPlayer(Lnet/minecraft/server/network/ServerPlayerEntity)V"), method = "onPlayerRespawned")
    private void eventOnPlayerRespawned(ServerPlayerEntity player, CallbackInfo callbackInfo) {
        PlayerEvents.PlayerRespawnEvent.EVENT.invoker().PlayerRespawn(player);
    }
     */


    @Inject(method = "addRespawnedPlayer", at = @At("TAIL"))
    private void eventOnPlayerRespawned(ServerPlayer player, CallbackInfo callbackInfo) {
        PlayerEvents.PLAYER_RESPAWN.invoker().onPlayerRespawn(player);
    }

    /*
    @Inject(method = "onPlayerConnected", at = @At("TAIL"))
    private void test(ServerPlayerEntity player, CallbackInfo callbackInfo) {
        EntityEvent<PlayerEntity> playerEvent = new EntityEvent<>(true, player);
        EventsTest.PLAYER_LOGIN.invoker().handleEvent(playerEvent);
        if (playerEvent.isCanceled()) callbackInfo.cancel();
    }
     */

    @Inject(method = "addNewPlayer", at = @At("TAIL"))
    private void eventOnPlayerConnected(ServerPlayer player, CallbackInfo callbackInfo) {
        PlayerEvents.PLAYER_LOGIN.invoker().onPlayerLogin(player);
    }

    @Inject(method = "addEntity", at = @At("RETURN"), cancellable = true)
    private void eventAddEntity(Entity entity, CallbackInfoReturnable<Boolean> callbackInfo) {
        EntityEvents.TRY_ADD_ENTITY.invoker().onTryAddEntity(entity, callbackInfo.getReturnValue());
        if (callbackInfo.getReturnValue()) {
            EntityEvents.ENTITY_ADDED.invoker().onEntityAdded(entity);
        }
    }

    /*
    private boolean addEntity(Entity entity) {
		if (entity.isRemoved()) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.getType()));
			return false;
		} else {
			return this.entityManager.addEntity(entity);
		}
	}
     */

}
