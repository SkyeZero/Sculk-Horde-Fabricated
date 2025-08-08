package com.github.sculkhorde.mixin.fabricated;

import com.github.sculkhorde.fabricated.MobTargetSelector;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Mob.class)
public abstract class MobMixin implements MobTargetSelector {
    @Shadow private GoalSelector targetSelector;
    @Override public GoalSelector getTargetSelector() {return targetSelector;}
}
