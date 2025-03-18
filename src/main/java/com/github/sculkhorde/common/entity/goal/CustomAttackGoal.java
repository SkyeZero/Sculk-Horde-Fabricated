package com.github.sculkhorde.common.entity.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class CustomAttackGoal extends Goal {
    protected final PathfinderMob mob;

    protected int ticksUntilNextAttackInterval;
    protected final int attackInterval = 60;
    protected long lastCanUseCheck;
    protected int ATTACK_DELAY;
    protected boolean isAttackInProgress = false;
    protected int ticksUntilAttackExecution = ATTACK_DELAY;
    protected float maxDistanceForAttack = 0;

    public CustomAttackGoal(PathfinderMob mob, float maxDistanceForAttackIn, int attackDelay) {
        this.mob = mob;
        ATTACK_DELAY = attackDelay;
        maxDistanceForAttack = maxDistanceForAttackIn;
    }

    public long getCanUseCheckInterval() {
        return 20;
    }

    public boolean canUse() {
        long gameTime = this.mob.level().getGameTime();
        if (gameTime - this.lastCanUseCheck < getCanUseCheckInterval()) {
            return false;
        }

        this.lastCanUseCheck = gameTime;
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        }

        return maxDistanceForAttack >= this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
    }

    public boolean canContinueToUse() {
        return canUse();
    }

    public void start() {
        this.ticksUntilNextAttackInterval = 0;
    }

    public void stop() {

    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }


    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return;
        }

        this.ticksUntilNextAttackInterval = Math.max(getTicksUntilNextAttackInterval() - 1, 0);

        if(!isAttackInProgress)
        {
            isAttackInProgress = true;
            triggerAnimation();
            return;
        }

        if(ticksUntilAttackExecution <= 0)
        {
            checkAndAttack(target);
            resetAttackCooldown();
        }
    }

    protected void triggerAnimation() {

    }


    protected void checkAndAttack(LivingEntity targetMob) {
        boolean isTargetNull = targetMob == null;
        if (isTargetNull) {
            return;
        }

        if (mob.isDeadOrDying() || targetMob.isDeadOrDying()) {
            return;
        }

        boolean isTooFarFromTarget = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(targetMob) > maxDistanceForAttack;
        boolean canSeeTarget = this.mob.getSensing().hasLineOfSight(targetMob);
        if (!isTimeToAttack() || isTooFarFromTarget || !canSeeTarget) {
            return;
        }

        mob.doHurtTarget(targetMob);
        onTargetHurt(targetMob);
    }

    protected void resetAttackCooldown() {
        ticksUntilNextAttackInterval = getAttackInterval();
        ticksUntilAttackExecution = ATTACK_DELAY;
        isAttackInProgress = false;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttackInterval <= 0;
    }

    protected int getTicksUntilNextAttackInterval() {
        return this.ticksUntilNextAttackInterval;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(attackInterval);
    }

    public void onTargetHurt(LivingEntity target) {

    }

}
