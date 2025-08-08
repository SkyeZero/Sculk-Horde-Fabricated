package com.github.sculkhorde.fabricated;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;

public class Ut {

    public static boolean isAreaLoaded(ServerLevel level, BlockPos center, int range) {
        return level.hasChunksAt(center.offset(-range, -range, -range), center.offset(range, range, range));
    }

    public static boolean isCorrectTierVanilla(Tier tier, BlockState state)
    {
        int i = tier.getLevel();
        if (i < 3 && state.is(BlockTags.NEEDS_DIAMOND_TOOL))
        {
            return false;
        }
        else if (i < 2 && state.is(BlockTags.NEEDS_IRON_TOOL))
        {
            return false;
        }
        else if (i < 1 && state.is(BlockTags.NEEDS_STONE_TOOL))
        {
            return false;
        }
        return true;
    }

}
