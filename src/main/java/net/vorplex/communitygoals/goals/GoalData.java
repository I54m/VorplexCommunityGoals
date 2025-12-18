package net.vorplex.communitygoals.goals;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class GoalData {

    @Getter
    private final String goalID;
    @Getter
    private final Map<Material, Integer> currentItems = new HashMap<>();
    @Getter
    private final Map<UUID, PlayerContribution> contributors = new HashMap<>();
    @Getter @Setter
    private boolean completed;

    public GoalData(@NotNull String GoalID, boolean completed, @NotNull Map<Material, Integer> currentItems, @NotNull Map<UUID, PlayerContribution> contributors) {
        this.goalID = Objects.requireNonNull(GoalID);
        this.completed = completed;
        this.currentItems.putAll(Objects.requireNonNull(currentItems));
        this.contributors.putAll(Objects.requireNonNull(contributors));
    }

}
