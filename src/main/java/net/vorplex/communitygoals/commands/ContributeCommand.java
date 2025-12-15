package net.vorplex.communitygoals.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.vorplex.communitygoals.VorplexCommunityGoals;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ContributeCommand {

    private static final VorplexCommunityGoals plugin = VorplexCommunityGoals.getInstance();

    public static LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("contribute")
                .requires(ctx -> ctx.getSender() instanceof Player)
                .then(Commands.argument("goal", StringArgumentType.word())
                        .suggests(ContributeCommand::getGoalSuggestions)
                        .executes(ContributeCommand::executeCommand)
                ).build();

    private static CompletableFuture<Suggestions> getGoalSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        plugin.GOAL_CONTROLLER.getGoals().forEach((goalID, goal) ->
            builder.suggest(goalID)
        );
        return builder.buildFuture();
    }

    private static int executeCommand(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final Player player = (Player) ctx.getSource().getSender();

        return Command.SINGLE_SUCCESS;
    }
}
