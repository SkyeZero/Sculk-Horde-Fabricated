package com.github.sculkhorde.common.block;

import com.github.sculkhorde.core.ModBlocks;
import com.github.sculkhorde.core.ModMobEffects;
import com.github.sculkhorde.util.BlockAlgorithms;
import com.github.sculkhorde.util.EntityAlgorithms;
import com.github.sculkhorde.util.TickUnits;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import org.lwjgl.glfw.GLFW;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class DiseasedKelpBlock extends Block implements LiquidBlockContainer {

    /*
     *  NOTE:
     *      In order for this block to render correctly, you must
     *      edit ClientModEventSubscriber.java to tell Minecraft
     *      to render this like a cutout.
     */

    /**
     * HARDNESS determines how difficult a block is to break<br>
     * 0.6f = dirt<br>
     * 1.5f = stone<br>
     * 2f = log<br>
     * 3f = iron ore<br>
     * 50f = obsidian
     */
    public static float HARDNESS = 0.6f;

    /**
     * BLAST_RESISTANCE determines how difficult a block is to blow up<br>
     * 0.5f = dirt<br>
     * 2f = wood<br>
     * 6f = cobblestone<br>
     * 1,200f = obsidian
     */
    public static float BLAST_RESISTANCE = 0.5f;

    /**
     * Denotes whether this is the end block or not.
     */
    public static final BooleanProperty END = BooleanProperty.create("end");

    /**
     * The Constructor that takes in properties
     * @param prop The Properties
     */
    public DiseasedKelpBlock(Properties prop) {
        super(prop);
        this.registerDefaultState(this.getStateDefinition().any().setValue(END, false));
    }

    /**
     * A simpler constructor that does not take in properties.<br>
     * I made this so that registering blocks in BlockRegistry.java can look cleaner
     */
    public DiseasedKelpBlock() {
        this(getProperties());
    }

    /**
     * Determines the properties of a block.<br>
     * I made this in order to be able to establish a block's properties from within the block class and not in the BlockRegistry.java
     * @return The Properties of the block
     */
    public static Properties getProperties()
    {
        return Properties.copy(ModBlocks.GRASS.get());
    }

    /**
     * Necessary for this to work.
     * @param builder
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(END);
    }

    public static boolean isEndBlock(BlockState pState)
    {
        return pState.getValue(END);
    }

    /** MODIFIERS **/

    public static void setEndBlock(Level pLevel, BlockState pState, BlockPos pPos, Boolean isEndBlock)
    {
        /**
         * Sets a block state into this world.Flags are as follows:
         * 1 will cause a block update.
         * 2 will send the change to clients.
         * 4 will prevent the block from being re-rendered.
         * 8 will force any re-renders to run on the main thread instead
         * 16 will prevent neighbor reactions (e.g. fences connecting, observers pulsing).
         * 32 will prevent neighbor reactions from spawning drops.
         * 64 will signify the block is being moved.
         * Flags can be OR-ed
         */
        if(pState.is(Blocks.KELP_PLANT) || pState.is(Blocks.KELP))
        {
            if(isEndBlock)
            {
                BlockAlgorithms.setBlockMisc(pLevel, pPos, Blocks.KELP.defaultBlockState());

            }
            else
            {
                BlockAlgorithms.setBlockMisc(pLevel, pPos, Blocks.KELP_PLANT.defaultBlockState());
            }
        }
        else
        {
            BlockAlgorithms.setBlockMisc(pLevel, pPos, pState.setValue(END, isEndBlock));
        }
    }

    public boolean isBlockAboveKelpOrDiseasedKelp(LevelReader level, BlockPos pos)
    {
        if(level.getBlockState(pos.above()).is(ModBlocks.DISEASED_KELP_BLOCK.get())
        || level.getBlockState(pos.above()).is(Blocks.KELP)
        || level.getBlockState(pos.above()).is(Blocks.KELP_PLANT))
        {
            return true;
        }

        return false;
    }

    public boolean isBlockBelowKelpOrDiseasedKelp(LevelReader level, BlockPos pos)
    {
        if(level.getBlockState(pos.below()).is(ModBlocks.DISEASED_KELP_BLOCK.get())
                || level.getBlockState(pos.below()).is(Blocks.KELP)
                || level.getBlockState(pos.below()).is(Blocks.KELP_PLANT))
        {
            return true;
        }

        return false;
    }

    // TODO: PORT OR REFORMAT INTO SAME AS KELP PLANT
    /*
    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {

        if (isBlockAboveKelpOrDiseasedKelp(level, pos)) {
            setEndBlock((Level) level, state, pos, false);
        } else {
            setEndBlock((Level) level, state, pos, true);
        }
    }
     */

    /** Makes entities slow and damages them. I stole this code from the berry bush.<br>
     * @param blockState The current blockstate
     * @param world The world this block si in
     * @param blockPos The position of this block
     * @param entity The entity inside
     */
    public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
        // If the entity is not a living entity, don't do anything
        if (!(entity instanceof LivingEntity) || world.isClientSide)
        {
            return;
        }

        // If the entity is a sculk, don't do anything
        if(EntityAlgorithms.isLivingEntityExplicitDenyTarget((LivingEntity) entity))
        {
            return;
        }

        LivingEntity vicitim = ((LivingEntity) entity);

        if(vicitim.getMaxHealth() / 2 >= vicitim.getHealth())
        {
            return;
        }

        entity.makeStuckInBlock(blockState, new Vec3(0.8F, 0.75D, (double)0.8F));
        entity.hurt(entity.damageSources().generic(), 1.0F);
        EntityAlgorithms.applyEffectToTarget(((LivingEntity) entity), ModMobEffects.SCULK_INFECTION.get(), TickUnits.convertSecondsToTicks(30), 0);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter iBlockReader, List<Component> tooltip, TooltipFlag flagIn) {

        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT))
        {
            tooltip.add(Component.translatable("tooltip.sculkhorde.diseased_kelp_block.functionality"));
        }
        else if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL))
        {
            tooltip.add(Component.translatable("tooltip.sculkhorde.diseased_kelp_block.lore"));
        }
        else
        {
            tooltip.add(Component.translatable("tooltip.sculkhorde.default"));
        }
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter p_54766_, BlockPos p_54767_, BlockState p_54768_, Fluid p_54769_) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor p_54770_, BlockPos p_54771_, BlockState p_54772_, FluidState p_54773_) {
        return false;
    }

    public BlockState updateShape(BlockState oldState, Direction dir, BlockState newState, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        return !oldState.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(oldState, dir, newState, level, pos, pos2);
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {

        if(levelReader.getFluidState(blockPos).isEmpty())
        {
            return false;
        }

        return levelReader.getBlockState(blockPos.below()).isSolid()
                || isBlockBelowKelpOrDiseasedKelp(levelReader, blockPos);
    }

    // TODO: INVESTIGATE
    /*
    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
        return true;
    }
     */

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        boolean isTop = !isBlockAboveKelpOrDiseasedKelp(level, pos);
        return this.defaultBlockState().setValue(END, isTop);
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState oldState, boolean idk) {
        super.onPlace(newState, level, pos, newState, idk);

        if(level.isClientSide)
        {
            return;
        }

        if (isBlockAboveKelpOrDiseasedKelp(level, pos)) {
            setEndBlock(level, newState, pos, false);
        } else {
            setEndBlock(level, newState, pos, true);
        }

        if(isBlockBelowKelpOrDiseasedKelp(level, pos))
        {
            setEndBlock(level, newState, pos.below(), false);
        }
    }

    @Override
    public void onRemove(BlockState newState, Level level, BlockPos pos, BlockState oldState, boolean p_60519_) {
        super.onRemove(newState, level, pos, oldState, p_60519_);

        if(level.isClientSide)
        {
            return;
        }

        if(isBlockBelowKelpOrDiseasedKelp(level, pos))
        {
            setEndBlock(level, oldState, pos.below(), true);
        }
    }

    public FluidState getFluidState(BlockState p_54319_) {
        return Fluids.WATER.getSource(false);
    }

    // #### Collision Code ####
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.1D, 0.0D, 16.0D, 15.9D, 16.0D);
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState p_221566_, BlockGetter p_221567_, BlockPos p_221568_) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collisionContext) {
        if (collisionContext instanceof EntityCollisionContext entityCollisionContext){
            if (entityCollisionContext.getEntity() instanceof LivingEntity livingEntity){
                return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, getter, pos, collisionContext);
    }

    @Override
    public boolean isPathfindable(BlockState p_154258_, BlockGetter p_154259_, BlockPos p_154260_, PathComputationType p_154261_) {
        return true;
    }
}
