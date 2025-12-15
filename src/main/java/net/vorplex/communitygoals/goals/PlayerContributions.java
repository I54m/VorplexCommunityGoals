package net.vorplex.communitygoals.goals;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerContributions {

    @Getter @NotNull
    private final String goalID;
    @Getter @NotNull
    private final UUID playerUUID;

    @Getter
    private final Map<Material, Integer> contributions = new HashMap<>();


    public PlayerContributions(@NotNull String goalID, @NotNull UUID playerUUID) {
        this.goalID = goalID;
        this.playerUUID = playerUUID;
    }

    public PlayerContributions(@NotNull Goal goal, @NotNull Player player) {
        this(goal.getGoalID(), player.getUniqueId());
    }

    public void AddContribution(@NotNull Map<Material, Integer> contribution) {
        contribution.keySet().forEach(material -> {
            contributions.put(material, contributions.get(material) + contribution.get(material));
        });
    }
}
