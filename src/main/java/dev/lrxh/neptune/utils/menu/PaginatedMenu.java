package dev.lrxh.neptune.utils.menu;

import org.bukkit.entity.Player;

import java.util.Map;

public abstract class PaginatedMenu extends Menu {

    //TODO: FINISH THIS
    @Override
    public String getTitle(Player player) {
        return getPaginatedTitle(player);
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        return getAllButtons(player);
    }

    public abstract String getPaginatedTitle(Player player);

    public abstract Map<Integer, Button> getAllButtons(Player player);

    public abstract int getMaxItemsPerPage(Player player);
}
