package com.github.sculkhorde.common.entity.goal;

import com.github.sculkhorde.common.entity.SculkGhastEntity;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class SculkGhastGoToAnchor extends Goal {
    private final SculkGhastEntity mob; // the skeleton mob
    private int timeToRecalcPath;
    private float speedModifier = 1.0F; // Doesn't actually do anything

    private final int IN_RANGE_OF_ANCHOR = 5;

    Path path;
    public SculkGhastGoToAnchor(SculkGhastEntity mob) {
        this.mob = mob;
    }

    private Mob getMob() {
        return (Mob) this.mob;
    }

    public boolean isInRangeOfAnchor()
    {
        return getMob().distanceToSqr(mob.getAnchorPoint()) < IN_RANGE_OF_ANCHOR;
    }

    @Override
    public boolean canUse() {

        boolean isAnchorNull = mob.getAnchorPoint() == null;
        boolean isNotScouting = !mob.isScouter();

        if(isAnchorNull || isNotScouting)
        {
            return false;
        }

        if (!mob.isAnchorPosValid(BlockPos.containing(mob.getAnchorPoint()))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void tick() {
        boolean isAnchorNull = mob.getAnchorPoint() == null;

        if(isAnchorNull)
        {
            return;
        }

        if (--this.timeToRecalcPath <= 0 || path == null)
        {
            this.timeToRecalcPath = this.adjustedTickDelay(TickUnits.convertSecondsToTicks(3));
            path = mob.getNavigation().createPath(mob.getAnchorPoint().x, mob.getAnchorPoint().y, mob.getAnchorPoint().z, 1);
            this.getMob().getNavigation().moveTo(path, speedModifier);
        }

    }

    @Override
    public void stop() {
        this.getMob().getNavigation().stop();
    }
}
