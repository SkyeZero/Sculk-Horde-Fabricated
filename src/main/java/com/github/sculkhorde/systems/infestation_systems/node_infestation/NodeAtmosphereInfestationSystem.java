package com.github.sculkhorde.systems.infestation_systems.node_infestation;

import com.github.sculkhorde.common.effect.DiseasedAtmosphereEffect;
import com.github.sculkhorde.core.ModConfig;
import com.github.sculkhorde.core.SculkHorde;
import com.github.sculkhorde.systems.chunk_cursor_system.ChunkCursorInfector;
import com.github.sculkhorde.util.BlockAlgorithms;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.server.ServerLifecycleHooks;

public class NodeAtmosphereInfestationSystem {
    // The parent tile entity
    protected BlockEntity parent = null;
    protected boolean isActive = false;
    protected long timeOfLastInfestationTick = 0;
    protected long INFESTATION_TICK_COOLDOWN = TickUnits.convertSecondsToTicks(10);
    protected long timeOfLastDiseasedAtmosphereTick = 0;
    protected long DISEASED_ATMOSPHERE_TICK_COOLDOWN = TickUnits.convertSecondsToTicks(10);

    protected int currentBlockInfestationRadius = 1;


    public NodeAtmosphereInfestationSystem(BlockEntity parent) {
        this.parent = parent;
        activate();
    }


    public void activate()
    {
        isActive = true;
    }

    public void deactivate()
    {
        isActive = false;
    }

    public void serverTick() {
        if(!isActive || parent.getLevel() == null)
        {
            return;
        }


        applyDiseasedAtmosphereTick();
        blockInfestationTick();
    }

    protected void applyDiseasedAtmosphereTick()
    {
        if(Math.abs(parent.getLevel().getGameTime() - timeOfLastDiseasedAtmosphereTick) < DISEASED_ATMOSPHERE_TICK_COOLDOWN)
        {
            return;
        }
        timeOfLastDiseasedAtmosphereTick = parent.getLevel().getGameTime();

        for(Player player: ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            if(BlockAlgorithms.getBlockDistance(player.blockPosition(), parent.getBlockPos()) < currentBlockInfestationRadius)
            {
                DiseasedAtmosphereEffect.applyToEntity(player, TickUnits.convertSecondsToTicks(15));
            }
        }
    }

    protected void blockInfestationTick()
    {
        if(!ModConfig.SERVER.block_infestation_enabled.get())
        {
            return;
        }

        if(Math.abs(parent.getLevel().getGameTime() - timeOfLastInfestationTick) < INFESTATION_TICK_COOLDOWN)
        {
            return;
        }

        timeOfLastInfestationTick = parent.getLevel().getGameTime();
        blockInfectionRectangle(currentBlockInfestationRadius);
        currentBlockInfestationRadius += 10;
    }

    protected int blockInfectionRectangle(int radius) {

        ChunkCursorInfector infector = ChunkCursorInfector.of()
                .level((ServerLevel) parent.getLevel())
                .center(parent.getBlockPos(), radius)
                .caveMode(false)
                .fillMode(false)
                .blocksPerTick(128)
                .fadeDistance(0);

        SculkHorde.chunkInfestationSystem.addChunkInfector(infector);
        return 0;
    }
}
