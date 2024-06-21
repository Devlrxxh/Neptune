package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.match.menu.button.HealthButton;
import dev.lrxh.neptune.match.menu.button.StatisticsButton;
import dev.lrxh.neptune.match.menu.button.SwitchInventoryButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.DisplayButton;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MatchSnapshotMenu extends Menu {

    private MatchSnapshot snapshot;

    @Override
    public String getTitle(Player player) {
        return "&8" + snapshot.getUsername() + "'s inventory";
    }

    @Override
    public Filters getFilter() {
        return Filters.FILL;
    }

    @Override
    public boolean getFixedPositions() {
        return false;
    }

    @Override
    public int getSize() {
        return 54;
    }


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] items = snapshot.getContents();

        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }

        buttons.put(48, new HealthButton(snapshot));
        buttons.put(50, new StatisticsButton(snapshot));

        if (this.snapshot.getOpponent() != null) {
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getOpponent(), snapshot));
            buttons.put(45, new SwitchInventoryButton(this.snapshot.getUsername(), snapshot));
        }

        return buttons;
    }

}