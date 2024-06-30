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
    public int getSize() {
        return maxItemsPerPage() + 9;
    }

    public abstract int maxItemsPerPage();
    public abstract Map<Integer, Button> getAllButtons(Player player);

    public int getPages(Player player) {
        int totalItems = getAllButtons(player).size();
        return (int) Math.ceil((double) totalItems / maxItemsPerPage());
    }

    public void modPage(Player player, int mod) {
        int newPage = page + mod;
        int totalPages = getPages(player);
        if (newPage > 0 && newPage <= totalPages) {
            page = newPage;
            openMenu(player.getUniqueId());
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> allButtons = getAllButtons(player);
        int maxItems = maxItemsPerPage();
        int totalItems = allButtons.size();
        int totalPages = getPages(player);

        Map<Integer, Button> pageButtons = new HashMap<>();

        int start = (page - 1) * maxItems;
        int end = Math.min(start + maxItems, totalItems);

        int index = 0;
        int buttonSlot = 9;

        for (Map.Entry<Integer, Button> entry : allButtons.entrySet()) {
            if (index >= start && index < end) {
                pageButtons.put(buttonSlot, entry.getValue());
                buttonSlot++;
            }
            index++;
        }

                pageButtons.put(0, new PageButton(-1, this));
                pageButtons.put(8, new PageButton(1, this));

        return pageButtons;
    }
}
