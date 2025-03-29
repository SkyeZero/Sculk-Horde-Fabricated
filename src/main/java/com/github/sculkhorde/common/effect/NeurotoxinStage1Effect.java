package com.github.sculkhorde.common.effect;

import com.github.sculkhorde.core.ModMobEffects;
import com.github.sculkhorde.util.ColorUtil;
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
import java.util.UUID;

public class NeurotoxinStage1Effect extends MobEffect implements IPotionExpireEffect{

    public static int liquidColor = ColorUtil.hexToRGB(ColorUtil.sculkAcidColor2);
    public static MobEffectCategory effectType = MobEffectCategory.HARMFUL;
    public long COOLDOWN = TickUnits.convertSecondsToTicks(1);
    public long cooldownTicksRemaining = COOLDOWN;
    private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    public static final UUID SPEED_MODIFIER_ID = UUID.fromString("2deaf4fc-1673-4c5b-ac4f-25e37e08760f");
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    public static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    protected Random random = new Random();


    /**
     * Old Dumb Constructor
     * @param effectType Determines if harmful or not
     * @param liquidColor The color in some number format
     */
    protected NeurotoxinStage1Effect(MobEffectCategory effectType, int liquidColor) {
        super(effectType, liquidColor);
    }

    /**
     * Simpler Constructor
     */
    public NeurotoxinStage1Effect() {
        this(effectType, liquidColor);
    }


    @Override
    public void applyEffectTick(LivingEntity sourceEntity, int amp) {

        if(sourceEntity.level().isClientSide())
        {
            return;
        }

        /*
        Attribute DAMAGE_ATTRIBUTE = Attributes.ATTACK_DAMAGE;

        if(sourceEntity.getAttributes().hasAttribute(DAMAGE_ATTRIBUTE))
        {
            AttributeInstance attributeinstance = sourceEntity.getAttribute(DAMAGE_ATTRIBUTE);

            sourceEntity.getAttribute(DAMAGE_ATTRIBUTE).addTransientModifier(new AttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, ));
        }

         */

        if(sourceEntity.hasEffect(MobEffects.WEAKNESS))
        {
            return;
        }

        sourceEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TickUnits.convertMinutesToTicks(1), 0));

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

        entity.addEffect(new MobEffectInstance(ModMobEffects.NEUROTOXIN_STAGE2.get(), TickUnits.convertMinutesToTicks(5), 0));
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
