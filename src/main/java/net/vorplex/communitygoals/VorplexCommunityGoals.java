package net.vorplex.communitygoals;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class VorplexCommunityGoals extends JavaPlugin {

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
        getComponentLogger().info(Component.text("       ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("        ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("        ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
        getComponentLogger().info(Component.text("Developed by I54m").color(NamedTextColor.RED));
        getComponentLogger().info(Component.text("v" + getPluginMeta().getVersion() + " Running on " + getServer().getVersion()).color(NamedTextColor.RED));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
        getComponentLogger().info(Component.text("Registering Commands...").color(NamedTextColor.GREEN));
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {

        });
        getComponentLogger().info(Component.text("Registered Commands!").color(NamedTextColor.GREEN));
        getComponentLogger().info(Component.text("Registering event listeners...").color(NamedTextColor.GREEN));

        getComponentLogger().info(Component.text("Registered event listeners!").color(NamedTextColor.GREEN));
        getComponentLogger().info(Component.text("Plugin loaded in: " + (System.nanoTime() - startTime) / 1000000 + "ms!").color(NamedTextColor.GREEN));
        getComponentLogger().info("───────────────────────────────────────────────────────────");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
