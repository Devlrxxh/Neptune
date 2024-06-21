package dev.lrxh.neptune.utils.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class PaginatedMenu extends Menu {

    private int page = 1;

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return getPaginatedTitle(player);
    }

    public final void modPage(Player player, int mod) {
        page += mod;
        buttons.clear();
        openMenu(player.getUniqueId());
    }

    public final int getPages(Player player) {
        int buttonAmount = getAllButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage(player));
    }

    @Override
    public final Map<Integer, Button> getButtons(Player player) {
        int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
        int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));
        int topIndex = 0;

        HashMap<Integer, Button> buttons = new HashMap<>();

        for (Map.Entry<Integer, Button> entry : getAllButtons(player).entrySet()) {
            int ind = entry.getKey();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 9;
                buttons.put(ind, entry.getValue());

                if (ind > topIndex) {
                    topIndex = ind;
                }
            }
        }

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        Map<Integer, Button> global = getGlobalButtons();

        if (global != null) {
            buttons.putAll(global);
        }

        return buttons;
    }

    public Map<Integer, Button> getGlobalButtons() {
        return null;
    }

    public abstract String getPaginatedTitle(Player player);

    public abstract Map<Integer, Button> getAllButtons(Player player);

    public abstract int getMaxItemsPerPage(Player player);
}
