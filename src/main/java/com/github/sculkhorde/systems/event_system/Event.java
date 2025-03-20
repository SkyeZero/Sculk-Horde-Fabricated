package com.github.sculkhorde.systems.event_system;

import com.github.sculkhorde.core.SculkHorde;
import com.github.sculkhorde.systems.event_system.events.HitSquadEvent;
import com.github.sculkhorde.systems.event_system.events.SpawnPhantomsEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class Event {
    protected UUID eventUUID;
    protected int eventCost;
    protected BlockPos eventLocation;
    protected long EXECUTION_COOLDOWN;
    protected long lastGameTimeOfEventExecution;

    protected ResourceKey<Level> dimension;
    protected boolean isEventReocurring = false;

    protected boolean isEventActive = false;
    protected boolean toBeRemoved = false;


    public Event(ResourceKey<Level> dimension)
    {
        this.dimension = dimension;
        setEventUUID(UUID.randomUUID());
    }

    // Getters and Setters
    public UUID getEventUUID()
    {
        return eventUUID;
    }

    // Logic

    public boolean canStart()
    {
        boolean hasEnoughTimePassed = getDimension().getGameTime() - lastGameTimeOfEventExecution >= EXECUTION_COOLDOWN;
        return hasEnoughTimePassed;
    }

    public boolean canContinue()
    {
        return false;
    }

    public void start()
    {
        SculkHorde.savedData.subtractSculkAccumulatedMass(eventCost);
        setEventActive(true);
    }

    public void serverTick()
    {

    }

    public void end()
    {
        if(!isEventReocurring)
        {
            toBeRemoved = true;
        }

        setEventActive(false);
        setLastGameTimeOfEventExecution(getDimension().getGameTime());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(!Event.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        return eventUUID == ((Event)obj).eventUUID;
    }

    public void setEventLocation(BlockPos eventLocation) {
        this.eventLocation = eventLocation;
    }

    public BlockPos getEventLocation() {
        return eventLocation;
    }

    protected Event setEventUUID(UUID eventUUID) {
        this.eventUUID = eventUUID;
        return this;
    }

    public Event setEventCost(int eventCost) {
        this.eventCost = eventCost;
        return this;
    }

    public Event setEXECUTION_COOLDOWN(long EXECUTION_COOLDOWN) {
        this.EXECUTION_COOLDOWN = EXECUTION_COOLDOWN;
        return this;
    }

    public Event setLastGameTimeOfEventExecution(long lastGameTimeOfEventExecution) {
        this.lastGameTimeOfEventExecution = lastGameTimeOfEventExecution;
        return this;
    }

    public Event setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
        return this;
    }

    public Event setEventReocurring(boolean isEventReocurring) {
        this.isEventReocurring = isEventReocurring;
        return this;
    }

    public Event setToBeRemoved(boolean toBeRemoved) {
        this.toBeRemoved = toBeRemoved;
        return this;
    }

    public Event setEventActive(boolean eventActive) {
        isEventActive = eventActive;
        return this;
    }

    public boolean isEventActive() {
        return isEventActive;
    }

    public int getEventCost() {
        return eventCost;
    }

    public long getEXECUTION_COOLDOWN() {
        return EXECUTION_COOLDOWN;
    }

    public long getLastGameTimeOfEventExecution() {
        return lastGameTimeOfEventExecution;
    }

    public ServerLevel getDimension()
    {
        return SculkHorde.savedData.level.getServer().getLevel(dimension);
    }

    public boolean isEventReoccurring() {
        return isEventReocurring;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    // Save and Load

    public void saveAdditional(CompoundTag tag)
    {

    }

    public void save(CompoundTag tag)
    {
        tag.putString("eventType", this.getClass().getName());

        //  This is to handle an edge case:
        //  Events used to have an ID that was a long, instead of a UUID.
        //  It's possible that during a version update, if an event in the save data does not have a UUID,
        //  It would just indefinitely crash.
        if(getEventUUID() != null)
        {
            tag.putUUID("eventID", getEventUUID());
        }
        else
        {
            tag.putUUID("eventID", UUID.randomUUID());
        }
        tag.putInt("eventCost", getEventCost());
        tag.putLong("EXECUTION_COOLDOWN", getEXECUTION_COOLDOWN());
        tag.putLong("lastGameTimeOfEventExecution", getLastGameTimeOfEventExecution());
        tag.putBoolean("isEventReoccurring", isEventReoccurring());
        tag.putBoolean("isEventActive", isEventActive());
        tag.putBoolean("toBeRemoved", isToBeRemoved());
        if(dimension != null) { tag.putString("dimension", dimension.location().toString()); }
        if(eventLocation != null) { tag.putLong("eventLocation", eventLocation.asLong()); }
    }

    public void loadAdditional(CompoundTag tag)
    {

    }

    public static Event load(CompoundTag tag)
    {
        Event event;
        ResourceKey<Level> dimensionResourceKey = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(tag.getString("dimension")));
        // DEFAULT CASE
        // Note:
        //  this constructor covers the edge case where an event does not have a UUID. The method creates one by default.
        //  See save method above for more context.
        event = new Event(dimensionResourceKey);

        if(tag.contains("eventType"))
        {
            String eventType = tag.getString("eventType");

            if(eventType.equals(HitSquadEvent.class.getName()))
            {
                event = new HitSquadEvent(dimensionResourceKey);
            }
            else if(eventType.equals(SpawnPhantomsEvent.class.getName()))
            {
                event = new SpawnPhantomsEvent(dimensionResourceKey);
            }
        }

        if(tag.contains("eventID")) { event.setEventUUID(tag.getUUID("eventID")); }
        if(tag.contains("eventCost")) { event.setEventCost(tag.getInt("eventCost")); }
        if(tag.contains("EXECUTION_COOLDOWN")) { event.setEXECUTION_COOLDOWN(tag.getLong("EXECUTION_COOLDOWN")); }
        if(tag.contains("lastGameTimeOfEventExecution")) { event.setLastGameTimeOfEventExecution(tag.getLong("lastGameTimeOfEventExecution")); }
        if(tag.contains("isEventReoccurring")) { event.setEventReocurring(tag.getBoolean("isEventReoccurring")); }
        if(tag.contains("isEventActive")) { event.setEventActive(tag.getBoolean("isEventActive")); }
        if(tag.contains("toBeRemoved")) { event.setToBeRemoved(tag.getBoolean("toBeRemoved")); }
        if(tag.contains("eventLocation")) { event.setEventLocation(BlockPos.of(tag.getLong("eventLocation"))); }

        event.loadAdditional(tag);

        return event;
    }
}
