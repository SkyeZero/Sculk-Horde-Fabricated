package com.github.sculkhorde.fabricated.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityEvents {

    public static final Event<TryAddEntityEvent> TRY_ADD_ENTITY = EventFactory.createArrayBacked(
            TryAddEntityEvent.class,
            (listeners) -> (entity, success) -> {
                for (TryAddEntityEvent event : listeners) {
                    event.onTryAddEntity(entity, success);
                }
            }
    );

    public static final Event<EntityAddedEvent> ENTITY_ADDED = EventFactory.createArrayBacked(
            EntityAddedEvent.class,
            (listeners) -> (entity) -> {
                for (EntityAddedEvent event : listeners) {
                    event.onEntityAdded(entity);
                }
            }
    );

    public static final Event<EntityDeathEvent> ENTITY_DEATH = EventFactory.createArrayBacked(
            EntityDeathEvent.class,
            (listeners) -> (entity, damageSource) -> {
                for (EntityDeathEvent event : listeners) {
                    event.onDeath(entity, damageSource);
                }
            }
    );

    public static final Event<LivingEntityDamageEvent> LIVING_DAMAGE = EventFactory.createArrayBacked(
            LivingEntityDamageEvent.class,
            (listeners) -> (event) -> {
                for (LivingEntityDamageEvent listener : listeners) {
                    listener.onLivingDamage(event);
                }
            }
    );

    public static final Event<MobEffectExpireEvent> EFFECT_EXPIRED = EventFactory.createArrayBacked(
            MobEffectExpireEvent.class,
            (listeners) -> (entity, mobEffectInstance) -> {
                for (MobEffectExpireEvent event : listeners) {
                    event.onExpired(entity, mobEffectInstance);
                }
            }
    );

    @FunctionalInterface
    public interface TryAddEntityEvent {
        void onTryAddEntity(Entity entity, boolean wasSuccess);
    }

    @FunctionalInterface
    public interface EntityAddedEvent {
        void onEntityAdded(Entity entity);
    }

    @FunctionalInterface
    public interface EntityDeathEvent {
        void onDeath(LivingEntity entity, DamageSource damageSource);
    }

    @FunctionalInterface
    public interface LivingEntityDamageEvent {
        void onLivingDamage(LivingDamageEvent event);
    }

    @FunctionalInterface
    public interface MobEffectExpireEvent {
        void onExpired(LivingEntity entity, MobEffectInstance mobEffectInstance);
    }

    public static class EntityEvent<T> {
        private final Entity entity;
        private T returnValue;
        public EntityEvent(Entity entity, T returnValue) {this.entity = entity; this.returnValue = returnValue;}
        public Entity getEntity() {return entity;}
        public T getReturnValue() {return returnValue;}
        public void setReturnValue(T value) {this.returnValue = value;}
    }

    public static class LivingDamageEvent extends EntityEvent<Float> {
        private final LivingEntity livingEntity;
        private final DamageSource source;

        public LivingDamageEvent(LivingEntity entity, DamageSource source, Float returnValue) {
            super(entity, returnValue);
            this.livingEntity = entity;
            this.source = source;
        }

        @Override public LivingEntity getEntity() {return livingEntity;}
        public DamageSource getSource() {return source;}

        public float getDamageValue() {return getReturnValue();}
        public void setDamageValue(float value) {setReturnValue(value);}
    }

}
