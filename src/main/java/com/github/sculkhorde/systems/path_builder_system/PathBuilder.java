package com.github.sculkhorde.systems.path_builder_system;

import com.github.sculkhorde.core.SculkHorde;
import com.github.sculkhorde.util.BlockAlgorithms;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.util.*;
import java.util.function.Predicate;

public class PathBuilder {
    private final PriorityQueue<BlockPos> queue = new PriorityQueue<>(Comparator.comparingInt(this::getHeuristic));
    private final Map<Long, Boolean> visitedPositions = new HashMap<>();
    private final Map<BlockPos, BlockPos> cameFrom = new HashMap<>();
    private boolean debugMode = false;
    private ArmorStand debugStand;

    private int MAX_DISTANCE = 150;

    protected Predicate<BlockPos> isObstructed;
    protected Predicate<BlockPos> isValidTargetBlock;

    protected Optional<PathBuilderRequest> request = Optional.empty();
    protected boolean foundTarget = false;

    public PathBuilder() {
    }

    public void enableDebugMode() {
        debugMode = true;
    }

    protected Optional<PathBuilderRequest> getCurrentRequest()
    {
        return request;
    }

    protected int getHeuristic(BlockPos pos) {

        if(request.isEmpty())
        {
            SculkHorde.LOGGER.error("PathBuilderSystem | Attempted to getHeuristic for non-existent request.");
            return -1;
        }

        return Math.abs(pos.getX() - request.get().getDesiredDestination().getX())
                + Math.abs(pos.getY() - request.get().getDesiredDestination().getY()
                +Math.abs(pos.getZ() - request.get().getDesiredDestination().getZ()));
    }

    protected void initializationTick()
    {

    }

    protected void processingTick()
    {
        PathBuilderRequest currentRequest = request.get();

        if (foundTarget || queue.isEmpty()) {

            if(debugMode)
            {
                SculkHorde.LOGGER.debug("PathBuilder | Found Target Block");
            }

            currentRequest.isPathBuildingInProgress = false;
            return;
        }

        // Spawn Debug Stand if Necessary
        if(debugStand == null && debugMode)
        {
            debugStand = new ArmorStand(currentRequest.getLevel(), currentRequest.getStartLocation().getX(), currentRequest.getStartLocation().getY(), currentRequest.getStartLocation().getZ());
            debugStand.setInvisible(true);
            debugStand.setNoGravity(true);
            debugStand.addEffect(new MobEffectInstance(MobEffects.GLOWING, TickUnits.convertHoursToTicks(1), 3));
            currentRequest.getLevel().addFreshEntity(debugStand);
        }

        BlockPos currentPos = queue.poll();

        if(debugMode)
        {
            debugStand.teleportTo(currentPos.getX() + 0.5, currentPos.getY(), currentPos.getZ() + 0.5);
        }

        if (isValidTargetBlock.test(currentPos)) {
            currentRequest.setPath(reconstructPath(currentPos));
            return;
        }

        for (BlockPos neighbor : BlockAlgorithms.getNeighborsCube(currentPos, false)) {
            if (visitedPositions.getOrDefault(neighbor.asLong(), false)) {
                continue;
            }

            if (isObstructed.test(neighbor)) {
                continue;
            }

            if(neighbor.distManhattan(currentRequest.getStartLocation()) > MAX_DISTANCE)
            {
                continue;
            }

            queue.add(neighbor);
            visitedPositions.put(neighbor.asLong(), true);
            cameFrom.put(neighbor, currentPos);

            if (debugMode) {
                //level.setBlockAndUpdate(neighbor, Blocks.GREEN_STAINED_GLASS.defaultBlockState());
            }
        }
    }

    protected void finishedTick()
    {

    }

    public void serverTick()
    {
        if(request.isEmpty())
        {
            return;
        }
        else if(!request.get().hasPathBuildingStarted)
        {
            initializationTick();
        }
        else if(request.get().hasPathBuildingStarted && request.get().isPathBuildingInProgress)
        {
            processingTick();
        }
        else if(request.get().hasPathBuildingStarted && !request.get().isPathBuildingInProgress)
        {
            finishedTick();
        }
    }

    private List<BlockPos> reconstructPath(BlockPos current) {
        List<BlockPos> path = new ArrayList<>();
        while (current != null) {
            path.add(current);
            current = cameFrom.get(current);
        }
        Collections.reverse(path);
        return path;
    }


    public void setTargetBlockPredicate(Predicate<BlockPos> predicate) {
        isValidTargetBlock = predicate;
    }

    public void setObstructionPredicate(Predicate<BlockPos> predicate) {
        isObstructed = predicate;
    }

    public void setMaxDistance(int value) {
        MAX_DISTANCE = value;
    }

}