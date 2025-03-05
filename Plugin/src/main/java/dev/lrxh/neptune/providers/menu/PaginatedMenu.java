package dev.lrxh.neptune.providers.menu;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class PaginatedMenu extends Menu {

    private int page = 1;

    public PaginatedMenu(String title, int size, Filter filter) {
        super(title, size, filter);
    }

    /**
     * Changes the page number
     *
     * @param player player viewing the inventory
     * @param mod    delta to modify the page number by
     */
    public final void modPage(Player player, int mod) {
        page += mod;
        getButtons(player).clear();
        open(player);
    }

    /**
     * @param player player viewing the inventory
     */
    public final int getPages(Player player) {
        int buttonAmount = getAllPagesButtons(player).size();

        if (buttonAmount == 0) {
            return 1;
        }

        return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage());
    }

    @Override
    public final List<Button> getButtons(Player player) {
        int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage());
        int maxIndex = (int) ((double) (page) * getMaxItemsPerPage());
        int topIndex = 0;

        List<Button> buttons = new ArrayList<>();

        for (Button button : getAllPagesButtons(player)) {
            int ind = button.getSlot();

            if (ind >= minIndex && ind < maxIndex) {
                ind -= (int) ((double) (getMaxItemsPerPage()) * (page - 1)) - 9;
                button.setSlot(ind);
                buttons.add(button);
                if (ind > topIndex) {
                    topIndex = ind;
                }
            }
        }

        buttons.add(new PageButton(0, -1, this));
        buttons.add(new PageButton(8, 1, this));


        List<Button> global = getGlobalButtons(player);

        if (global != null) {
            buttons.addAll(global);
        }

        return buttons;
    }

    public int getMaxItemsPerPage() {
        return 18;
    }

    public List<Button> getGlobalButtons(Player player) {
        return null;
    }

    public abstract List<Button> getAllPagesButtons(Player player);

}