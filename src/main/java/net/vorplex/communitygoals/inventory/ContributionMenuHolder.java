package net.vorplex.communitygoals.inventory;

import net.vorplex.communitygoals.VorplexCommunityGoals;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class ContributionMenuHolder implements InventoryHolder {

    private final Inventory inventory;

    public ContributionMenuHolder(VorplexCommunityGoals plugin) {
        this.inventory = plugin.getServer().createInventory(this, 54);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }


}
