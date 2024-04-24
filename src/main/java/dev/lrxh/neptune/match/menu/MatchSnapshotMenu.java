package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.utils.InventoryUtil;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.DisplayButton;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MatchSnapshotMenu extends Menu {

    private MatchSnapshot snapshot;

    @Override
    public String getTitle(Player player) {
        return "&7Match Inventory";
    }

    @Override
    public Filters getFilter() {
        return Filters.FILL;
    }

    public boolean getFixedPositions() {
        return false;
    }

    public boolean resetCursor() {
        return false;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(snapshot.getContents());

        for (int i = 0; i < fixedContents.length; i++) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }


        buttons.put(48, new HealthButton((int) snapshot.getHealth()));
        buttons.put(50, new StatisticsButton(snapshot));

        if (this.snapshot.getOpponent() != null) {
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getOpponent()));
            buttons.put(45, new SwitchInventoryButton(this.snapshot.getUsername()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class HealthButton extends Button {

        private int health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(PlayerUtil.getPlayerHead(snapshot.getUuid()))
                    .name("&7Player Stats")
                    .lore(Arrays.asList(
                            "&8| &7Health: &c" + health + StringEscapeUtils.unescapeJava("❤"),
                            "&8| &7Hunger: &e" + snapshot.getHunger() + "&7/20"
                    ))
                    .clearFlags()
                    .build();
        }

    }


    @AllArgsConstructor
    private class StatisticsButton extends Button {

        private MatchSnapshot snapshot;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&7Match Stats")
                    .lore(Arrays.asList(
                            "&8| &7Hits: &a" + snapshot.getTotalHits(),
                            "&8| &7Longest Combo: &a" + snapshot.getLongestCombo(),
                            "&8| &7Ping: &a" + snapshot.getPing() + " ms"
                    ))
                    .clearFlags()
                    .build();
        }

    }

    @AllArgsConstructor
    private class SwitchInventoryButton extends Button {

        private String opponent;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.LEVER)
                    .name("&7Press to switch to " + opponent + " inventory.")
                    .lore("&aClick to Switch")
                    .clearFlags()
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.chat("/viewinv " + snapshot.getOpponent());
        }
    }

}