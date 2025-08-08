package com.github.sculkhorde.core;

import com.github.sculkhorde.common.advancement.*;
import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements {

    public static void init() {
        CriteriaTriggers.register(GravemindEvolveImmatureTrigger.INSTANCE);
        CriteriaTriggers.register(GravemindEvolveMatureTrigger.INSTANCE);
        CriteriaTriggers.register(SculkHordeStartTrigger.INSTANCE);
        CriteriaTriggers.register(SculkNodeSpawnTrigger.INSTANCE);
        CriteriaTriggers.register(SoulHarvesterTrigger.INSTANCE);
        CriteriaTriggers.register(SculkHordeDefeatTrigger.INSTANCE);
        CriteriaTriggers.register(ContributeTrigger.INSTANCE);
    }

}
