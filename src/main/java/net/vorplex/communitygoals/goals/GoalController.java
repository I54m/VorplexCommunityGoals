package net.vorplex.communitygoals.goals;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.vorplex.communitygoals.VorplexCommunityGoals;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GoalController {

    private final VorplexCommunityGoals PLUGIN;
    @Getter
    private final Map<String, Goal> Goals = new HashMap<>();

    public GoalController(VorplexCommunityGoals plugin) {
        this.PLUGIN = plugin;
    }

    public Goal getGoal(@NotNull String goalID) {
        return Goals.get(goalID);
    }

    public void removeGoal(@NotNull String goalID) {
        Goals.remove(goalID);
    }

    public void SaveGoals() {
        PLUGIN.getComponentLogger().info(Component.text("Saving Goals data...").color(NamedTextColor.GREEN));
        boolean errors = false;
        for (Goal goal : Goals.values()) {
            try {
                SaveGoalData(goal);
            } catch (Exception e) {
                errors = true;
                PLUGIN.getComponentLogger().error("An error occurred while saving data for goal: {}", goal.getName());
                PLUGIN.getComponentLogger().error(e.getMessage());
            }
        }
        if (errors) PLUGIN.getComponentLogger().warn(Component.text("Errors occurred while saving goals data. Some data might not be saved!!").color(NamedTextColor.RED));
        else PLUGIN.getComponentLogger().info(Component.text("Successfully saved Goals data!").color(NamedTextColor.GREEN));
    }

    private void SaveGoalData(@NotNull Goal goal)  throws Exception {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File goals_file = new File(PLUGIN.GOALS_DATA_DIRECTORY, goal.getGoalID()+ ".json");
        final FileWriter writer = new FileWriter(goals_file, false);
        gson.toJson(goal.getGoalData(), writer);
        // ensure writer is flushed and closed correctly
        writer.flush();
        writer.close();
    }

    public void LoadGoals() {
        if (!PLUGIN.GOALS_DATA_DIRECTORY.exists()) {
            PLUGIN.getComponentLogger().info(Component.text("Goals data directory was not found, Creating...").color(NamedTextColor.YELLOW));
            if (!PLUGIN.getDataFolder().exists()) PLUGIN.getDataFolder().mkdir();
            if (!PLUGIN.GOALS_DATA_DIRECTORY.exists()) PLUGIN.GOALS_DATA_DIRECTORY.mkdir();
            PLUGIN.getComponentLogger().info(Component.text("Created Goals data directory!").color(NamedTextColor.GREEN));
        }

        PLUGIN.getComponentLogger().info(Component.text("Attempting to load Goals from config...").color(NamedTextColor.GREEN));
        Objects.requireNonNull(PLUGIN.CONFIG.getConfigurationSection("goals")).getKeys(false).forEach(goalID -> {
            Map<Material, Integer> reqItems = new HashMap<>();
            Objects.requireNonNull(PLUGIN.getConfig().getConfigurationSection("goals." + goalID + ".required-items")).getKeys(false).forEach(itemID -> {
                Material material = Material.matchMaterial(itemID);
                if (material == null){
                    material = Material.BARRIER;
                    PLUGIN.getComponentLogger().error("Material " + itemID + " is not a valid material for goal " + goalID);
                }
                reqItems.put(material, PLUGIN.getConfig().getInt("goals." + goalID + ".required-items." + itemID));
            });

            ConfigurationSection goalConfig = Objects.requireNonNull(PLUGIN.CONFIG.getConfigurationSection("goals." + goalID));
            Goal goal = new Goal(goalID,
                    Objects.requireNonNull(goalConfig.getString("name")),
                    Objects.requireNonNull(goalConfig.getString("description")),
                    goalConfig.getBoolean("enabled"),
                    goalConfig.getString("start-time"),
                    goalConfig.getString("end-time"),
                    reqItems);
            try {
                goal.setGoalData(LoadGoalData(goalID));
            } catch (FileNotFoundException e) {
                PLUGIN.getComponentLogger().error("Unable to find data file for Goal: {}", goalID);
            }
            Goals.put(goal.getGoalID(), goal);
        });
    }

    private GoalData LoadGoalData(@NotNull String goalID) throws FileNotFoundException {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File file = new File(PLUGIN.GOALS_DATA_DIRECTORY, goalID + ".json");
        final FileReader reader = new FileReader(file);
        GoalData json = gson.fromJson(reader, GoalData.class);
        if (json != null) {
            return json;
        } else PLUGIN.getComponentLogger().warn(Component.text("File " + file.toPath() + " was empty - skipping").color(NamedTextColor.YELLOW));
        return new GoalData(goalID, false, new HashMap<>(), new HashMap<>());
    }


}
