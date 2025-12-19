package net.vorplex.communitygoals.goals;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerContribution {

    @Getter @NotNull
    private final String goalID;
    @Getter @NotNull
    private final UUID playerUUID;

    @Getter
    private final Map<Material, Integer> contributions = new HashMap<>();


    public PlayerContribution(@NotNull String goalID, @NotNull UUID playerUUID) {
        this.goalID = goalID;
        this.playerUUID = playerUUID;
    }

    public PlayerContribution(@NotNull Goal goal, @NotNull Player player) {
        this(goal.getGoalID(), player.getUniqueId());
    }

    public void AddItems(@NotNull Map<Material, Integer> contribution) {
        //TODO prevent over contributing - throw exception
        contribution.forEach((material, amount) -> {
            contributions.merge(material, amount, Integer::sum);
        });
    }
}
