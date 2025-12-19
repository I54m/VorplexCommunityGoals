package net.vorplex.communitygoals.inventory;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.vorplex.communitygoals.VorplexCommunityGoals;
import net.vorplex.communitygoals.goals.Goal;
import net.vorplex.communitygoals.goals.PlayerContribution;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.*;

public class ContributionMenuHolder implements InventoryHolder, Listener {

    private final NumberFormat nf = NumberFormat.getIntegerInstance(Locale.US);
    private final Style defaultStyle = Style.style().color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false).build();

    private final Inventory inventory;
    private final ItemStack dividerItem;
    private final ItemStack controlItem;
    //TODO better control item lore
    private List<Component> controlItemLore = new ArrayList<>();
    private final List<ItemStack> goalItems;
    private final Goal goal;


    public ContributionMenuHolder(Goal goal) {
        this.goal = Objects.requireNonNull(goal, "goal cannot be null");
        this.inventory = VorplexCommunityGoals.getInstance().getServer().createInventory(this, 54, Component.text(goal.getName()));

        dividerItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        dividerItem.setData(DataComponentTypes.CUSTOM_NAME, Component.text(" "));

        controlItem = new ItemStack(Material.NETHER_STAR, 1);
        controlItem.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Contributing towards " + goal.getName()).style(defaultStyle));

        goalItems = new ArrayList<>();

        goal.getRequiredItems().forEach(((material, integer) -> {
            ItemStack goalItem = new ItemStack(material);
            Component name = goalItem.displayName();
            if (goalItem.getType() != Material.BARRIER)
                goalItem.setData(DataComponentTypes.CUSTOM_NAME, Component.text(nf.format(integer) + "x ERROR: Unknown Item!!").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false));
            else
                goalItem.setData(DataComponentTypes.CUSTOM_NAME, Component.text(nf.format(integer) + "x ").append(name).style(defaultStyle));
            goalItems.add(new ItemStack(material));
        }));
        if (goalItems.size() > 9)
            throw new IllegalStateException("Too many goal items for single-row GUI");

        // Fill top 2 rows with dividers
        for (int i = 0; i < 18; i++)
            inventory.setItem(i, dividerItem);

        controlItemLore.add(Component.text("/\\ Required Items").style(defaultStyle));
        controlItemLore.add(Component.text("\\/ Items to contribute").style(defaultStyle));
        controlItemLore.add(Component.text("% completed").style(defaultStyle));
        controlItemLore.add(buildProgressBar(0));
    }

    @Override
    public @NotNull Inventory getInventory() {

        Map<Material, Integer> itemsContributed = goal.getItemsContributed();
        Map<Material, Integer> totalItemsRequired = goal.getRequiredItems();
        Map<Material, Double> completionPercentages = goal.getCompletionPercentages();

        goalItems.forEach(goalItem -> {
            Material material = goalItem.getType();
            double completionPercentage = completionPercentages.get(material);

            goalItem.setData(DataComponentTypes.LORE, ItemLore.lore()
                    .addLine(Component.text(nf.format(itemsContributed.get(material)) + "/" + nf.format(totalItemsRequired.get(material))).style(defaultStyle))
                    .addLine(Component.text( completionPercentage + "% Complete").style(defaultStyle))
                    .addLine(buildProgressBar(completionPercentage))
                    .build()
            );
        });



        //TODO blank center spot if % 2
        // Center goal items
        int startSlot = (9 - goalItems.size()) / 2;
        for (int i = 0; i < goalItems.size(); i++) {
            inventory.setItem(startSlot + i, goalItems.get(i));
        }

        controlItemLore.set(2, Component.text(goal.getTotalCompletionPercent()*100 + "% completed").style(defaultStyle));
        controlItemLore.set(3, buildProgressBar(goal.getTotalCompletionPercent()));

        controlItem.setData(DataComponentTypes.LORE, ItemLore.lore().lines(controlItemLore));

        this.inventory.setItem(13, controlItem);

        return inventory;
    }

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


    private Component buildProgressBar(double progress) {
        int barLength = 20;

        progress = Math.max(0.0, Math.min(1.0, progress));
        int completed = (int) Math.floor(progress * barLength);
        int remaining = barLength - completed;

        String bar =
                "<gray>[<green>"
                        + "|".repeat(completed)
                        + "<yellow>"
                        + "|".repeat(remaining)
                        + "<gray>]";

        return MiniMessage.miniMessage().deserialize(bar).decoration(TextDecoration.ITALIC, false);
    }


}
