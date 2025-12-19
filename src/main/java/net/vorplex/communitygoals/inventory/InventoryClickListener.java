package net.vorplex.communitygoals.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null || !(inventory.getHolder(false) instanceof ContributionMenuHolder)) return;
        //prevent moving the divider items, control item or any of the goal items
        if (event.getCurrentItem() != null && (
                event.getCurrentItem().equals(dividerItem) ||
                        event.getCurrentItem().equals(controlItem) ||
                        goalItems.contains(event.getCurrentItem())
        )) {
            event.setCancelled(true);
            return;
        }

        if (!goal.getRequiredItems().containsKey(event.getCurrentItem().getType())) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(Component.text("That item is not needed for this goal!").color(NamedTextColor.RED));
        }
    }
}
