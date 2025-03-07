package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Menu {
    protected final int size;
    protected final boolean updateOnClick;
    private final String title;
    private final Filter filter;
    private List<Button> buttons;

    public Menu(String title, int size, Filter filter) {
        this.title = title;
        this.size = size;
        this.filter = filter;
        this.updateOnClick = false;
    }

    public Menu(int size, Filter filter) {
        this.title = "";
        this.size = size;
        this.filter = filter;
        this.updateOnClick = false;
    }

    public Menu(String title, int size, Filter filter, boolean updateOnClick) {
        this.title = title;
        this.size = size;
        this.filter = filter;
        this.updateOnClick = updateOnClick;
    }

    public abstract List<Button> getButtons(Player player);

    public String getTitle(Player player) {
        return "";
    }

    private void set(Inventory inventory, int slot, ItemStack itemStack) {
        if (slot < inventory.getSize()) {
            inventory.setItem(slot, itemStack);
        } else {
            ServerUtils.error("Menu: " + title + " slot (" + slot + ") is larger than inventory size: (" + inventory.getSize() + ")");
        }
    }

    public void open(Player player) {
        String title;
        if (this.title.isEmpty()) {
            title = getTitle(player);
        } else {
            title = this.title;
        }

        Inventory inventory = Bukkit.createInventory(player, size, Component.text(CC.color(title)));

        buttons = getButtons(player);
        switch (filter) {
            case FILL -> {
                for (int i = 0; i < inventory.getSize(); i++) {
                    if (getButton(i) == null) {
                        buttons.add(new DisplayButton(i, Material.GRAY_STAINED_GLASS_PANE, ""));
                    }
                }
            }
        }


        for (Button button : buttons) {
            set(inventory, button.getSlot(), button.getItemStack(player));
        }

        player.openInventory(inventory);
        player.updateInventory();

        MenuService.get().add(player, this);
    }

    public Button getButton(int slot) {
        for (Button button : buttons) {
            if (button.getSlot() == slot) return button;
        }

        return null;
    }
}
