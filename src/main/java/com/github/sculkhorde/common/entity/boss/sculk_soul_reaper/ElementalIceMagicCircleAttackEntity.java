package com.github.sculkhorde.common.entity.boss.sculk_soul_reaper;

import com.github.sculkhorde.core.ModEntities;
import com.github.sculkhorde.util.TickUnits;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ElementalIceMagicCircleAttackEntity extends ElementalFireMagicCircleAttackEntity {


    public ElementalIceMagicCircleAttackEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ElementalIceMagicCircleAttackEntity(Level level) {
        this(ModEntities.ELEMENTAL_ICE_MAGIC_CIRCLE.get(), level);
    }

    public ElementalIceMagicCircleAttackEntity(Level level, double x, double y, double z, float angle, LivingEntity owner) {
        this(level);
        setPos(x,y,z);
        this.setYRot(angle * (180F / (float)Math.PI));
        setOwner(owner);
    }

    protected void applyEffect(LivingEntity entity)
    {
        boolean didHurt = entity.hurt(damageSources().magic(), DAMAGE);

        if(!didHurt)
        {
            return;
        }

        entity.setTicksFrozen(TickUnits.convertSecondsToTicks(10));

        if(getOwner() != null)
        {
            entity.hurt(getOwner().damageSources().magic(), DAMAGE);
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TickUnits.convertSecondsToTicks(10), 0), getOwner());
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TickUnits.convertSecondsToTicks(10), 0), getOwner());
        }
        else
        {
            entity.hurt(damageSources().magic(), DAMAGE);
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, TickUnits.convertSecondsToTicks(10), 0));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TickUnits.convertSecondsToTicks(10), 0));
        }

    }
}
