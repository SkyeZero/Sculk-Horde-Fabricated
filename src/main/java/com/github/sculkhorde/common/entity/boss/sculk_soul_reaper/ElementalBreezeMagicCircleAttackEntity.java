package com.github.sculkhorde.common.entity.boss.sculk_soul_reaper;

import com.github.sculkhorde.core.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ElementalBreezeMagicCircleAttackEntity extends ElementalFireMagicCircleAttackEntity {


    public ElementalBreezeMagicCircleAttackEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public ElementalBreezeMagicCircleAttackEntity(Level level) {
        this(ModEntities.ELEMENTAL_BREEZE_MAGIC_CIRCLE.get(), level);
    }

    public ElementalBreezeMagicCircleAttackEntity(Level level, double x, double y, double z, float angle, LivingEntity owner) {
        this(level);
        setPos(x,y,z);
        this.setYRot(angle * (180F / (float)Math.PI));
        setOwner(owner);
    }

    protected void applyEffect(LivingEntity entity)
    {

        double damageResistance = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        double d1 = Math.max(0.0D, 1.0D - damageResistance);
        entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.3D * d1, 0.0D));

        boolean didHurt = entity.hurt(damageSources().magic(), DAMAGE);
        if(!didHurt)
        {
            return;
        }


        //double damageResistance = entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        //double d1 = Math.max(0.0D, 1.0D - damageResistance);
        //entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, 0.6D * d1, 0.0D));


        if(getOwner() != null)
        {
            entity.hurt(getOwner().damageSources().magic(), DAMAGE);
        }
        else
        {
            entity.hurt(damageSources().magic(), DAMAGE);
        }

    }

}
