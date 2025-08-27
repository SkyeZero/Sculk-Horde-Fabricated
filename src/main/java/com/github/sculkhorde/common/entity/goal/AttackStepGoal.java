package com.github.sculkhorde.common.entity.goal;

import com.github.sculkhorde.common.entity.entity_debugging.IDebuggableGoal;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.Optional;

public class AttackStepGoal extends Goal implements IDebuggableGoal
{
    protected boolean isAttackStepComplete = false;
    protected String lastReasonOfNoStart = "None";
    protected AttackSequenceGoal sequenceParent;

    protected AttackSequenceGoal getSequenceParent()
    {
        return sequenceParent;
    }

    protected void setSequenceParent(AttackSequenceGoal parent)
    {
        sequenceParent = parent;
    }


    @Override
    public void start() {
        super.start();
        setAttackStepComplete(false);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !isAttackStepComplete();
    }

    @Override
    public void stop() {
        super.stop();
        setAttackStepComplete(false);
    }

    @Override
    public Optional<String> getLastReasonForGoalNoStart() {
        return Optional.of(lastReasonOfNoStart);
    }

    @Override
    public Optional<String> getGoalName() {
        return Optional.empty();
    }

    @Override
    public long getLastTimeOfGoalExecution() {
        return -1;
    }

    @Override
    public long getTimeRemainingBeforeCooldownOver() {
        return -1;
    }

    public boolean isAttackStepComplete() {
        return isAttackStepComplete;
    }

    public void setAttackStepComplete(boolean attackStepComplete) {
        isAttackStepComplete = attackStepComplete;
    }
}
