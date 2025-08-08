package com.github.sculkhorde.core;

import com.github.sculkhorde.common.command.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(ModCommands::registerSubCommands);
    }

    public static void registerSubCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal(SculkHorde.MOD_ID)
                .then(MassCommand.register(dispatcher, buildContext))
                .then(GravemindCommand.register(dispatcher, buildContext))
                .then(StatusCommand.register(dispatcher, buildContext))
                .then(StatusAllCommand.register(dispatcher, buildContext))
                .then(RaidCommand.register(dispatcher, buildContext))
                .then(StatisticsCommand.register(dispatcher, buildContext))
                .then(PlayerStatusCommand.register(dispatcher, buildContext))
                .then(ConfigCommand.register(dispatcher, buildContext))
                .then(SummonReinforcementsCommand.register(dispatcher, buildContext))
                .then(NodesStatusCommand.register(dispatcher, buildContext))
                .then(VesselCommand.register(dispatcher, buildContext))
                .then(ResetCommand.register(dispatcher, buildContext))
                .then(SoulReaperCommand.register(dispatcher, buildContext))
                .then(HitSquadCommand.register(dispatcher, buildContext))
                .then(DevCommand.register(dispatcher, buildContext));
        dispatcher.register(cmd);
    }
}
