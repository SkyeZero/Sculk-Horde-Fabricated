package com.github.sculkhorde.common.effect;

import com.github.sculkhorde.core.ModMobEffects;
import com.github.sculkhorde.util.EntityAlgorithms;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.MobEffectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NeurotoxinStage2Effect extends MobEffect implements IPotionExpireEffect{

    public static int liquidColor = 338997;
    public static MobEffectCategory effectType = MobEffectCategory.HARMFUL;
    public long COOLDOWN = TickUnits.convertSecondsToTicks(1);
    public long cooldownTicksRemaining = COOLDOWN;
    protected Random random = new Random();


    /**
     * Old Dumb Constructor
     * @param effectType Determines if harmful or not
     * @param liquidColor The color in some number format
     */
    protected NeurotoxinStage2Effect(MobEffectCategory effectType, int liquidColor) {
        super(effectType, liquidColor);
    }

    /**
     * Simpler Constructor
     */
    public NeurotoxinStage2Effect() {
        this(effectType, liquidColor);
    }


    @Override
    public void applyEffectTick(LivingEntity sourceEntity, int amp) {

        if(sourceEntity.level().isClientSide())
        {
            return;
        }

        if(sourceEntity.hasEffect(MobEffects.WEAKNESS) &&
                sourceEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) &&
                sourceEntity.hasEffect(MobEffects.DIG_SLOWDOWN))
        {
            return;
        }

        sourceEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TickUnits.convertMinutesToTicks(1), 0));
        sourceEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TickUnits.convertMinutesToTicks(1), 0));
        sourceEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, TickUnits.convertMinutesToTicks(1), 0));

    }

    @Override
    public void onPotionExpire(MobEffectEvent.Expired event)
    {
        if(event.getEntity().level().isClientSide()) { return;}

        LivingEntity entity = event.getEntity();
        // OR mob outside of world border
        if(entity == null || EntityAlgorithms.isSculkLivingEntity.test(entity))
        {
            return;
        }

        entity.addEffect(new MobEffectInstance(ModMobEffects.NEUROTOXIN_STAGE3.get(), TickUnits.convertMinutesToTicks(5), 0));
    }

    /**
     * A function that is called every tick an entity has this effect. <br>
     * I do not use because it does not provide any useful inputs like
     * the entity it is affecting. <br>
     * I instead use ForgeEventSubscriber.java to handle the logic.
     * @param ticksLeft The amount of ticks remaining
     * @param amplifier The level of the effect
     * @return Determines if the effect should apply.
     */
    @Override
    public boolean isDurationEffectTick(int ticksLeft, int amplifier) {
        if(cooldownTicksRemaining > 0)
        {
            cooldownTicksRemaining--;
            return false;
        }
        cooldownTicksRemaining = COOLDOWN;
        return true;

    }

    @Override
    public List<ItemStack> getCurativeItems() {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        return ret;
    }

}
