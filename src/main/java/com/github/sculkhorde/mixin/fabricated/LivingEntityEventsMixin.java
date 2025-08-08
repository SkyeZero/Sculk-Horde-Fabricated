package com.github.sculkhorde.mixin.fabricated;

import com.github.sculkhorde.fabricated.events.EntityEvents;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityEventsMixin {

    // net.minecraft.entity.LivingEntity.onStatusEffectRemoved
    // net.minecraft.world.entity.LivingEntity.onEffectRemoved
    // net.minecraft.world.effect.MobEffectInstance

    // net.minecraft.world.entity.LivingEntity.tickEffects
    // net.minecraft.world.effect.MobEffectInstance

    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void eventOnStatusEffectRemoved(MobEffectInstance statusEffectInstance, CallbackInfo callbackInfo) {
        EntityEvents.EFFECT_EXPIRED.invoker().onExpired((LivingEntity) (Object) this, statusEffectInstance);
    }

    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance)V"), method = "tickEffects")
    private void eventOnStatusEffectRemoved(CallbackInfo callbackInfo, @Local MobEffectInstance statusEffectInstance) {
        EntityEvents.EFFECT_EXPIRED.invoker().onExpired((LivingEntity) (Object) this, statusEffectInstance);
    }
     */

    /*

    @Inject(method = "die", at = @At("HEAD"))
    private void eventOnDeath(DamageSource damageSource, CallbackInfo callbackInfo) {
        EntityEvents.ENTITY_DEATH.invoker().onDeath((LivingEntity) (Object) this, damageSource);
    }

    @Inject(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V", shift = At.Shift.BY, by = -2))
    private void eventOnApplyDamage(DamageSource source, float amount, @Local LocalRef<Float> var9, CallbackInfo callbackInfo) {
        FabricEvents.LivingDamageEvent event = new FabricEvents.LivingDamageEvent((LivingEntity) (Object) this, source, var9.get());
        FabricEvents.LIVING_DAMAGE.invoker().onLivingDamage(event);
        var9.set(event.getDamageValue());
    }
     */

}
