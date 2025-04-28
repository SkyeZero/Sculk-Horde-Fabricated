package com.github.sculkhorde.common.entity;

import com.github.sculkhorde.common.entity.components.ImprovedFlyingNavigator;
import com.github.sculkhorde.common.entity.components.TargetParameters;
import com.github.sculkhorde.common.entity.goal.*;
import com.github.sculkhorde.util.EntityAlgorithms;
import com.github.sculkhorde.util.SquadHandler;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class SculkGhastEntity extends FlyingMob implements GeoEntity, ISculkSmartEntity {

    /**
     * In order to create a mob, the following files were created/edited.<br>
     * Edited core/ EntityRegistry.java<br>
     * Edited util/ ModEventSubscriber.java<br>
     * Edited client/ ClientModEventSubscriber.java<br>
     * Added client/model/entity/ SculkPhantomModel.java<br>
     * Added client/renderer/entity/ SculkPhantomRenderer.java
     */

    //The Health
    public static final float MAX_HEALTH = 15F;
    //The armor of the mob
    public static final float ARMOR = 5F;
    //ATTACK_DAMAGE determines How much damage it's melee attacks do
    public static final float ATTACK_DAMAGE = 6F;
    //ATTACK_KNOCKBACK determines the knockback a mob will take
    public static final float ATTACK_KNOCKBACK = 1F;
    //FOLLOW_RANGE determines how far away this mob can see and chase enemies
    public static final float FOLLOW_RANGE = 32F;
    //MOVEMENT_SPEED determines how far away this mob can see other mobs
    public static final float MOVEMENT_SPEED = 0.20F;

    // Controls what types of entities this mob can target
    protected final TargetParameters TARGET_PARAMETERS = new TargetParameters(this).enableTargetHostiles().disableTargetingEntitiesInWater();

    protected BlockPos anchorPoint = BlockPos.ZERO;
    protected Vec3 moveTargetPoint = new Vec3(anchorPoint.getX(), anchorPoint.getY(), anchorPoint.getZ());

    protected ArrayList<BlockPos> searchPositions = new ArrayList<>();
    protected Vec3 spawnPoint = null;
    protected boolean isScouter = false;

    protected final double MAX_MOB_MASS_STORED = 1000D;
    protected final ArrayList<Mob> storedMobs = new ArrayList<>();

    /**
     * The Constructor
     * @param type The Mob Type
     * @param worldIn The world to initialize this mob in
     */
    public SculkGhastEntity(EntityType<? extends SculkGhastEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }


    /**
     * Determines & registers the attributes of the mob.
     * @return The Attributes
     */
    public static AttributeSupplier.Builder createAttributes()
    {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ARMOR, ARMOR)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK)
                .add(Attributes.FOLLOW_RANGE,FOLLOW_RANGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FLYING_SPEED, 2F)
                .add(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get(), 0.0);
    }



    /**
     * Registers Goals with the entity. The goals determine how an AI behaves ingame.
     * Each goal has a priority with 0 being the highest and as the value increases, the priority is lower.
     * You can manually add in goals in this function, however, I made an automatic system for this.
     */
    @Override
    public void registerGoals() {

        Goal[] goalSelectorPayload = goalSelectorPayload();
        for(int priority = 0; priority < goalSelectorPayload.length; priority++)
        {
            this.goalSelector.addGoal(priority, goalSelectorPayload[priority]);
        }

        Goal[] targetSelectorPayload = targetSelectorPayload();
        for(int priority = 0; priority < targetSelectorPayload.length; priority++)
        {
            this.targetSelector.addGoal(priority, targetSelectorPayload[priority]);
        }
    }

    /**
     * Prepares an array of goals to give to registerGoals() for the goalSelector.<br>
     * The purpose was to make registering goals simpler by automatically determining priority
     * based on the order of the items in the array. First element is of priority 0, which
     * represents highest priority. Priority value then increases by 1, making each element
     * less of a priority than the last.
     * @return Returns an array of goals ordered from highest to lowest piority
     */
    public Goal[] goalSelectorPayload()
    {
        return new Goal[]{
                new Despawn(this, TickUnits.convertMinutesToTicks(15)),
                //new selectRandomLocationToVisit(),
                //new SculkGhastGoToAnchor(this),
                new DropOffMobsNearHostiles(),
                new FindAndStoreIdleMobs(),
                new SculkGhastWanderGoal(this, 1.0F, TickUnits.convertSecondsToTicks(3), 10)
        };
    }

    /**
     * Prepares an array of goals to give to registerGoals() for the targetSelector.<br>
     * The purpose was to make registering goals simpler by automatically determining priority
     * based on the order of the items in the array. First element is of priority 0, which
     * represents highest priority. Priority value then increases by 1, making each element
     * less of a priority than the last.
     * @return Returns an array of goals ordered from highest to lowest piority
     */
    public Goal[] targetSelectorPayload()
    {
        return new Goal[]{
                new InvalidateTargetGoal(this),
                new TargetAttacker(this),
                new NearestLivingEntityTargetGoal<>(this, true, false)
        };
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        ImprovedFlyingNavigator flyingpathnavigation = new ImprovedFlyingNavigator(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    /** Getters and Setters **/

    public double getStoredMobMass()
    {
        double result = 0;
        for(Mob e : storedMobs)
        {
            if(!e.getAttributes().hasAttribute(Attributes.MAX_HEALTH))
            {
                continue;
            }

            result += e.getAttributes().getValue(Attributes.MAX_HEALTH);
        }

        return result;
    }

    public boolean canStoreMob(Mob entity)
    {
        if(entity == null || entity.isDeadOrDying())
        {
            return false;
        }

        if(entity.getUUID().equals(getUUID()))
        {
            return false;
        }

        if(!EntityAlgorithms.isSculkLivingEntity.test((entity)) && !EntityAlgorithms.isLivingEntityAllyToSculkHorde(entity))
        {
            return false;
        }

        if(entity.getTarget() != null)
        {
            return false;
        }

        if(getStoredMobMass() >= MAX_MOB_MASS_STORED)
        {
            return false;
        }

        if(!getSensing().hasLineOfSight(entity))
        {
            return false;
        }

        return true;
    }

    public void storeMob(Mob entity)
    {
        storedMobs.add(entity);
        entity.discard();
    }

    public Vec3 getAnchorPoint() {
        return this.anchorPoint.getCenter();
    }

    @Override
    public SquadHandler getSquad() {
        return null;
    }

    @Override
    public boolean isParticipatingInRaid() {
        return false;
    }

    @Override
    public void setParticipatingInRaid(boolean isParticipatingInRaidIn) {
    }

    @Override
    public TargetParameters getTargetParameters() {
        return TARGET_PARAMETERS;
    }

    public double getPassengersRidingOffset() {
        return this.getEyeHeight();
    }

    public boolean isIdle() {
        return getTarget() == null;
    }

    public boolean isScouter() {
        return isScouter;
    }

    public void setScouter(boolean isScouter) {
        this.isScouter = isScouter;
    }

    /** Attributes **/

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        // Calculate the difference between the entity's position and the camera's position
        double deltaX = this.getX() - cameraX;
        double deltaY = this.getY() - cameraY;
        double deltaZ = this.getZ() - cameraZ;
        // Calculate the squared distance between the entity and the camera
        double squaredDistance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
        // Return true if the entity is within the rendering distance, false otherwise
        return this.shouldRenderAtSqrDistance(squaredDistance);
    }
    @Override
    public boolean shouldRenderAtSqrDistance(double squaredDistance) {

        // Get the size of the entity's bounding box
        double size = this.getBoundingBox().getSize();
        // If the size is not a valid number, set it to 1.0
        if (Double.isNaN(size)) {
            size = 1.0D;
        }

        // Multiply the size by a constant factor and the view scale
        size *= 64.0D * getViewScale();
        // Return true if the squared distance is less than the cubed of the size, false otherwise
        return squaredDistance < size * size * size;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }


    @Override
    public void checkDespawn() {}

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public boolean dampensVibrations() {
        return true;
    }

    protected float getStandingEyeHeight(@NotNull Pose p_33136_, EntityDimensions p_33137_) {
        return p_33137_.height * 0.35F;
    }

    // #### Functions ####

    public void releaseMob()
    {
        if(storedMobs.isEmpty())
        {
            return;
        }

        Mob savedEntity = storedMobs.get(0);
        Mob spawnedEntity = (Mob) savedEntity.getType().spawn((ServerLevel) level(), blockPosition(), MobSpawnType.MOB_SUMMONED);
        spawnedEntity.setTarget(getTarget());
        spawnedEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, TickUnits.convertSecondsToTicks(10), 0));
        spawnedEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, TickUnits.convertSecondsToTicks(10), 1));
        spawnedEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, TickUnits.convertSecondsToTicks(10), 0));
        storedMobs.remove(0);
    }

    // This method allows the entity to travel in a given direction
    @Override
    public void travel(@NotNull Vec3 direction) {
        // If the entity is controlled by the local player
        if (this.isControlledByLocalInstance()) {
            // Move the entity relative to its orientation and the direction vector
            this.moveRelative(getTarget() == null ? 0.04F : 0.05F, direction);

            // Move the entity according to its current velocity
            this.move(MoverType.SELF, this.getDeltaMovement());

            // If the entity is in water, reduce its velocity by 10%
            if (this.isInWater()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9F));
                // If the entity is in lava, reduce its velocity by 40%
            } else if (this.isInLava()) {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.6F));
            }
            else
            {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.95F));
            }
        }

        // Update the entity's animation based on its movement
        this.calculateEntityAnimation(false);
    }

    /** Events **/

    public void tick()
    {
        super.tick();
        if (this.level().isClientSide)
        {
            return;
        }

        String customDebugName = "";
        for(Goal goal : goalSelector.getRunningGoals().toList())
        {
            customDebugName += goal.getClass().getSimpleName();
            customDebugName += " | ";
        }

        setCustomName(Component.literal(customDebugName));

    }

    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor p_33126_, @NotNull DifficultyInstance p_33127_, @NotNull MobSpawnType p_33128_, @Nullable SpawnGroupData p_33129_, @Nullable CompoundTag p_33130_) {
        this.anchorPoint = this.blockPosition().above(5);
        return super.finalizeSpawn(p_33126_, p_33127_, p_33128_, p_33129_, p_33130_);
    }

    protected @NotNull BodyRotationControl createBodyControl() {
        return new GhastBodyRotationControl(this);
    }

    /** Save Data **/

    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AX")) {
            this.anchorPoint = new BlockPos(tag.getInt("AX"), tag.getInt("AY"), tag.getInt("AZ"));
        }
        isScouter = tag.getBoolean("scouter");
    }

    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AX", this.anchorPoint.getX());
        tag.putInt("AY", this.anchorPoint.getY());
        tag.putInt("AZ", this.anchorPoint.getZ());
        tag.putBoolean("scouter", isScouter);

    }

    /** Animation **/
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        //controllers.add(DefaultAnimations.genericWalkIdleController(this).transitionLength(5));
        //controllers.add(new AnimationController<>(this, "blob_idle", 5, this::poseTumorCycle));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /** Sounds **/

    protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
        return SoundEvents.GHAST_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_DEATH;
    }


    public boolean isAnchorPosValid(BlockPos pos)
    {
        boolean isThereIsNoFluid = level().getFluidState(pos).isEmpty() && level().getFluidState(pos.below()).isEmpty();
        boolean isItFarEnoughAway = distanceToSqr(Vec3.atCenterOf(pos)) > 30;
        // As long as its not a fluid, its valid
        return isThereIsNoFluid && isItFarEnoughAway;
    }



    protected class DropOffMobsNearHostiles extends Goal
    {
        protected long timeOfLastPathRecalculation = 0;

        public DropOffMobsNearHostiles()
        {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {

            if(getTarget() == null)
            {
                return false;
            }

            if(storedMobs.isEmpty())
            {
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
            super.start();

            if(getTarget() != null) { navigation.moveTo(getTarget(), 1.0F);}
        }

        @Override
        public void tick() {
            super.tick();

            if(storedMobs.isEmpty() || getTarget() == null)
            {
                return;
            }

            float pathRecalculationCooldown;
            float distanceFromTarget = EntityAlgorithms.getDistanceBetweenEntities(SculkGhastEntity.this, getTarget());
            if(distanceFromTarget <= 5)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(0.5F);
            }
            else if(distanceFromTarget <= 10)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(1F);
            }
            else if(distanceFromTarget <= 32)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(3F);
            }
            else
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(6F);
            }

            if(level().getGameTime() - timeOfLastPathRecalculation >= pathRecalculationCooldown)
            {
                navigation.moveTo(getTarget(), 1.0F);
                timeOfLastPathRecalculation = level().getGameTime();
            }


            if(EntityAlgorithms.getDistanceBetweenEntities(getTarget(), SculkGhastEntity.this) < getBbWidth() * 2)
            {
                releaseMob();
            }
        }
    }

    protected class FindAndStoreIdleMobs extends Goal
    {
        protected List<Entity> targets;

        protected long timeOfLastPathRecalculation = 0;
        protected long timeOfLastSearch = 0;
        protected final long MOB_SEARCH_COOLDOWN = TickUnits.convertSecondsToTicks(5);

        public FindAndStoreIdleMobs()
        {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
            setFlags(EnumSet.of(Flag.TARGET));
        }

        public Predicate<Entity> canStoreMobPredicate = (entity) ->
        {
            if(entity instanceof Mob mobEntity)
            {
                return canStoreMob(mobEntity);
            }

            return false;
        };

        @Override
        public boolean canUse() {

            if(getTarget() != null)
            {
                return false;
            }

            if(getStoredMobMass() >= MAX_MOB_MASS_STORED)
            {
                return false;
            }

            if(level().getGameTime() - timeOfLastSearch >= MOB_SEARCH_COOLDOWN)
            {
                AABB searchBox = EntityAlgorithms.createBoundingBoxCubeAtBlockPos(position(), 256);
                targets = EntityAlgorithms.getEntitiesInBoundingBox((ServerLevel) level(), searchBox, canStoreMobPredicate);
                timeOfLastSearch = level().getGameTime();
            }

            if(targets.isEmpty())
            {
                return false;
            }

            return true;
        }

        public void cleanTargets()
        {
            ArrayList<Entity> targetsToRemove = new ArrayList<>();

            for(Entity target : targets)
            {
                if(canStoreMob((Mob) target))
                {
                    continue;
                }

                targetsToRemove.add(target);
            }

            for(Entity target : targetsToRemove)
            {
                targets.remove(target);
            }
        }

        @Override
        public boolean canContinueToUse() {

            cleanTargets();

            if(targets.isEmpty())
            {
                return false;
            }

            return getStoredMobMass() < MAX_MOB_MASS_STORED;
        }

        @Override
        public void start() {
            super.start();

            navigation.moveTo(targets.get(0), 1.0F);
        }

        @Override
        public void tick() {
            super.tick();

            if(targets.isEmpty())
            {
                return;
            }

            Mob target = (Mob) targets.get(0);

            float pathRecalculationCooldown;
            float distanceFromTarget = EntityAlgorithms.getDistanceBetweenEntities(SculkGhastEntity.this, target);
            if(distanceFromTarget <= 5)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(0.5F);
            }
            else if(distanceFromTarget <= 10)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(1F);
            }
            else if(distanceFromTarget <= 32)
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(3F);
            }
            else
            {
                pathRecalculationCooldown = TickUnits.convertSecondsToTicks(6F);
            }

            if(level().getGameTime() - timeOfLastPathRecalculation >= pathRecalculationCooldown)
            {
                navigation.moveTo(targets.get(0), 1.0F);
                timeOfLastPathRecalculation = level().getGameTime();
            }


            if(EntityAlgorithms.getDistanceBetweenEntities(target, SculkGhastEntity.this) < getBbWidth() * 2)
            {
                storeMob(target);
                targets.remove(0);
            }
        }
    }

    protected class Despawn extends DespawnAfterTime
    {
        public Despawn(ISculkSmartEntity mob, int ticksThreshold) {
            super(mob, ticksThreshold);
        }

        public long calculateTicksThreshold()
        {
            if(isScouter()) { return ticksThreshold; }

            return ticksThreshold/3;
        }

        @Override
        public boolean canUse()
        {
            boolean mobHasBeenNameTagged = ((Mob) mob).hasCustomName();
            if(level.getGameTime() - creationTime > calculateTicksThreshold() && !mob.isParticipatingInRaid() && !mobHasBeenNameTagged)
            {
                return true;
            }
            return false;
        }

        @Override
        public void start()
        {
            if(isScouter()) { discard(); }
        }
    }

    protected class GhastBodyRotationControl extends BodyRotationControl {
        public GhastBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        public void clientTick() {
            SculkGhastEntity.this.yHeadRot = SculkGhastEntity.this.yBodyRot;
            SculkGhastEntity.this.yBodyRot = SculkGhastEntity.this.getYRot();
        }
    }
}
