package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty {

    private static final DynamicCommandExceptionType ERROR_ALREADY_DIFFICULT = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.difficulty.failure", object);
    });

    public CommandDifficulty() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = net.minecraft.commands.CommandDispatcher.literal("difficulty");
        EnumDifficulty[] aenumdifficulty = EnumDifficulty.values();
        int i = aenumdifficulty.length;

        for (int j = 0; j < i; ++j) {
            EnumDifficulty enumdifficulty = aenumdifficulty[j];

            literalargumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal(enumdifficulty.getKey()).executes((commandcontext) -> {
                return setDifficulty((CommandListenerWrapper) commandcontext.getSource(), enumdifficulty);
            }));
        }

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) literalargumentbuilder.requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            EnumDifficulty enumdifficulty1 = ((CommandListenerWrapper) commandcontext.getSource()).getLevel().getDifficulty();

            ((CommandListenerWrapper) commandcontext.getSource()).sendSuccess(IChatBaseComponent.translatable("commands.difficulty.query", enumdifficulty1.getDisplayName()), false);
            return enumdifficulty1.getId();
        }));
    }

    public static int setDifficulty(CommandListenerWrapper commandlistenerwrapper, EnumDifficulty enumdifficulty) throws CommandSyntaxException {
        MinecraftServer minecraftserver = commandlistenerwrapper.getServer();
        net.minecraft.server.level.WorldServer worldServer = commandlistenerwrapper.getLevel(); // CraftBukkit

        if (worldServer.getDifficulty() == enumdifficulty) { // CraftBukkit
            throw CommandDifficulty.ERROR_ALREADY_DIFFICULT.create(enumdifficulty.getKey());
        } else {
            worldServer.serverLevelData.setDifficulty(enumdifficulty); // CraftBukkit
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.difficulty.success", enumdifficulty.getDisplayName()), true);
            return 0;
        }
    }
}
