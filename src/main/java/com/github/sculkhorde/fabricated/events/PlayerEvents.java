package com.github.sculkhorde.fabricated.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerEvents {

    public static final Event<PlayerRespawn> PLAYER_RESPAWN = EventFactory.createArrayBacked(
            PlayerRespawn.class,
            (listeners) -> (player) -> {
                for (PlayerRespawn playerEvent : listeners) {
                    playerEvent.onPlayerRespawn(player);
                }
            }
    );

    public static final Event<PlayerLogin> PLAYER_LOGIN = EventFactory.createArrayBacked(
            PlayerLogin.class,
            (listeners) -> (player) -> {
                for (PlayerLogin listener : listeners) {
                    listener.onPlayerLogin(player);
                }
            }
    );

    public static final Event<PlayerStartTick> PLAYER_START_TICK = EventFactory.createArrayBacked(
            PlayerStartTick.class,
            (listeners) -> (player) -> {
                for (PlayerStartTick listener : listeners) {
                    listener.onTick(player);
                }
            }
    );

    public static final Event<PlayerEndTick> PLAYER_END_TICK = EventFactory.createArrayBacked(
            PlayerEndTick.class,
            (listeners) -> (player) -> {
                for (PlayerEndTick listener : listeners) {
                    listener.onTick(player);
                }
            }
    );

    @FunctionalInterface
    public interface PlayerRespawn {
        void onPlayerRespawn(ServerPlayer player);
    }

    @FunctionalInterface
    public interface PlayerLogin {
        void onPlayerLogin(ServerPlayer player);
    }

    @FunctionalInterface
    public interface PlayerStartTick {
        void onTick(Player player);
    }

    @FunctionalInterface
    public interface PlayerEndTick {
        void onTick(Player player);
    }

}
