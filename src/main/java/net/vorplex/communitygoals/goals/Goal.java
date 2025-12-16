package net.vorplex.communitygoals.goals;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Goal {

    /* Below are defined in config.yml */
    @Getter @NotNull
    private final String goalID;
    @Getter @NotNull
    private final String name;
    @Getter @NotNull
    private final String description;
    @Getter
    private final boolean enabled;
    @Getter
    private final long startTime;
    @Getter
    private final long endTime;
    @Getter
    private final Map<Material, Integer> requiredItems = new HashMap<>();

    /* Goal data object stored in a json file in the data directory */
    @Getter @Setter @Nullable
    private GoalData goalData;

    public boolean isActive() {
        return System.currentTimeMillis() >= startTime && System.currentTimeMillis() <= endTime;
    }

    public Goal(@NotNull String goalID, @NotNull String name, @NotNull String description, boolean enabled, String startTime, String endTime, @NotNull Map<Material, Integer> requiredItems) {
        this.goalID = Objects.requireNonNull(goalID);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.enabled = enabled;
        this.startTime = parseTime(startTime);
        this.endTime = parseTime(endTime);
        if (this.endTime < this.startTime) throw new IllegalArgumentException("End time cannot be less than start time");
        this.requiredItems.putAll(Objects.requireNonNull(requiredItems));
    }

    private long parseTime(String input) {
        return Instant.parse(input).toEpochMilli();
    }

    public void AddContribution(@NotNull PlayerContributions contribution) {
        Objects.requireNonNull(goalData);
        if (contribution.getGoalID().equals(this.goalID)) throw new IllegalArgumentException("This contribution is not for this goal!");
        if (goalData.getContributors().containsKey(contribution.getPlayerUUID()))
            goalData.getContributors().get(contribution.getPlayerUUID()).AddContribution(contribution.getContributions());
        else goalData.getContributors().put(contribution.getPlayerUUID(), contribution);

        contribution.getContributions().keySet().forEach(contributionMaterial -> {
            if (!requiredItems.containsKey(contributionMaterial)) throw new IllegalArgumentException("Item " + contributionMaterial + " is no required by this goal!");
            int amount = goalData.getCurrentItems().getOrDefault(contributionMaterial, 0);
            amount += contribution.getContributions().get(contributionMaterial);
            goalData.getCurrentItems().put(contributionMaterial, amount);
        });
        CheckForCompletion();
    }


    public void RemoveAllContributions(@NotNull UUID playerUUID) {
        Objects.requireNonNull(goalData);
        Map<Material, Integer> toRemove = goalData.getContributors().get(playerUUID).getContributions();
        toRemove.keySet().forEach(material -> goalData.getCurrentItems().compute(material, (k, current) -> current - toRemove.get(material)));
    }

    public void CheckForCompletion() {
        Objects.requireNonNull(goalData);
        Map<Material, Boolean> completed = new HashMap<>();
        requiredItems.forEach((material, required) -> completed.put(material, goalData.getCurrentItems().getOrDefault(material, 0).equals(required)));
        this.goalData.setCompleted(!completed.containsValue(false));
    }

    public Map<Material, Double> getCompletionPercentages() {
        Objects.requireNonNull(goalData);
        Map<Material, Double> percentages = new HashMap<>();
        requiredItems.forEach((material, required) -> {
            if (required <= 0) {
                percentages.put(material, 1.0);
                return;
            }

            double progress = (double) goalData.getCurrentItems().getOrDefault(material, 0) / required;
            percentages.put(material, Math.min(1.0, progress));
        });
        return percentages;
    }

    public double getTotalCompletionPercent() {
        Objects.requireNonNull(goalData);
        Map<Material, Double> percentages = getCompletionPercentages();
        double weightedProgress = 0;
        int totalRequired = 0;

        for (Material material : percentages.keySet()) {
            int required = requiredItems.get(material);
            weightedProgress += percentages.get(material) * required;
            totalRequired += required;
        }

        return totalRequired == 0 ? 1.0 : weightedProgress / totalRequired;
    }
}