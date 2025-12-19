package net.vorplex.communitygoals.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.vorplex.communitygoals.goals.PlayerContribution;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InventoryCloseListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof ContributionMenuHolder)) return;
        Map<Material, Integer> itemAmounts = new HashMap<>();
        Arrays.stream(inventory.getContents()).filter(content ->
                content!= null &&
                        !content.equals(dividerItem) &&
                        !content.equals(controlItem) &&
                        !goalItems.contains(content)
        ).forEach(content -> {
            if (itemAmounts.containsKey(content.getType()))
                itemAmounts.put(content.getType(), itemAmounts.get(content.getType()) + content.getAmount());
            else
                itemAmounts.put(content.getType(), content.getAmount());
        });

        PlayerContribution contribution = new PlayerContribution(goal, player);
        contribution.AddItems(itemAmounts);
        goal.AddContribution(contribution);
        //TODO more user feedback post contribution
        player.sendMessage(Component.text("Your contribution has been added to the total goal!").color(NamedTextColor.GREEN));
        for (int i = 18; i < inventory.getSize(); i++)
            inventory.setItem(i, null);
    }
}
