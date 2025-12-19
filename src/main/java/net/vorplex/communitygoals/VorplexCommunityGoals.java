package net.vorplex.communitygoals;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.vorplex.communitygoals.commands.ContributeCommand;
import net.vorplex.communitygoals.goals.GoalController;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VorplexCommunityGoals extends JavaPlugin {

    @Getter @Setter
    private static VorplexCommunityGoals instance;

    public final File GOALS_DATA_DIRECTORY = new File(getDataFolder(), "goals-data");
    public final GoalController GOAL_CONTROLLER = new GoalController(this);
    public final FileConfiguration CONFIG = getConfig();

    @Override
    public void onEnable() {
        long startTime = System.nanoTime();
        getComponentLogger().info("");
        getComponentLogger().info(Component.text("██╗   ██╗ ██████╗ ██████╗ ██████╗ ██╗     ███████╗██╗  ██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("██║   ██║██╔═══██╗██╔══██╗██╔══██╗██║     ██╔════╝╚██╗██╔╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("██║   ██║██║   ██║██████╔╝██████╔╝██║     █████╗   ╚███╔╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("╚██╗ ██╔╝██║   ██║██╔══██╗██╔═══╝ ██║     ██╔══╝   ██╔██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text(" ╚████╔╝ ╚██████╔╝██║  ██║██║     ███████╗███████╗██╔╝ ██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("  ╚═══╝   ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚══════╝╚══════╝╚═╝  ╚═╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("          __   __                          ___    ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("         /  ` /  \\  |\\/|  |\\/| |  | |\\ | |  |  \\ /").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("         \\__, \\__/  |  |  |  | \\__/ | \\| |  |   | ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("                   __   __             __         ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("                  / _` /  \\  /\\  |    /__`        ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("                  \\__> \\__/ /~~\\ |___ .__/        ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
        getComponentLogger().info(Component.text("Developed by I54m").color(NamedTextColor.RED));
        getComponentLogger().info(Component.text("v" + getPluginMeta().getVersion() + " Running on " + getServer().getVersion()).color(NamedTextColor.RED));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
        setInstance(this);
        if (!new File(getDataFolder(), "config.yml").exists())
            saveDefaultConfig();
        GOAL_CONTROLLER.LoadGoals();
        //todo scheduled task to check goals for completions and check end time
        getComponentLogger().info(Component.text("Registering Commands...").color(NamedTextColor.GREEN));
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ContributeCommand.COMMAND_NODE);
        });
        getComponentLogger().info(Component.text("Registered Commands!").color(NamedTextColor.GREEN));
        getComponentLogger().info(Component.text("Registering event listeners...").color(NamedTextColor.GREEN));
        GOAL_CONTROLLER.getGoals().forEach((goalID, goal) -> 
                getServer().getPluginManager().registerEvents(goal.getContributionMenu(), this)
        );
        getComponentLogger().info(Component.text("Registered event listeners!").color(NamedTextColor.GREEN));
        getComponentLogger().info(Component.text("Plugin loaded in: " + (System.nanoTime() - startTime) / 1000000 + "ms!").color(NamedTextColor.GREEN));
        getComponentLogger().info("───────────────────────────────────────────────────────────");

    }

    @Override
    public void onDisable() {
        GOAL_CONTROLLER.SaveGoals();
    }
}
