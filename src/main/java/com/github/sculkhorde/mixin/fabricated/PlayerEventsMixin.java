package com.github.sculkhorde.mixin.fabricated;

import com.github.sculkhorde.fabricated.events.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEventsMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void eventPlayerTickStart(CallbackInfo callbackInfo) {
        PlayerEvents.PLAYER_START_TICK.invoker().onTick((Player)(Object) this);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void eventPlayerTickEnd(CallbackInfo callbackInfo) {
        PlayerEvents.PLAYER_END_TICK.invoker().onTick((Player)(Object) this);
    }

}
