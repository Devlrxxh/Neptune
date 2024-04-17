package dev.lrxh.neptune.utils.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class Menu {

    public static Map<String, Menu> currentlyOpenedMenus = new HashMap<>();

    protected Neptune plugin = Neptune.get();
    private Map<Integer, Button> buttons = new HashMap<>();
    private boolean autoUpdate = false;
    private boolean updateAfterClick = true;
    private boolean closedByMenu = false;
    private ItemStack fillerType;
    private int size = 9;
    private Filters filter;
    private boolean fixedPositions = true;
    private boolean resetCursor;

    {

        fillerType = new ItemBuilder(XMaterial.matchXMaterial(MenusLocale.FILTER_MATERIAL.getString()).get().parseMaterial()).name(MenusLocale.FILTER_NAME.getString()).durability(MenusLocale.FILTER_DURABILITY.getInt()).amount(1, false).build();
    }

    private void fillBorder(Inventory inventory) {
        int size = inventory.getSize();

        if (size < 9) return;

        ItemStack fillerItem = this.fillerType;

        for (int i = 1; i <= 7 && size >= 18; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerItem);
                inventory.setItem(size - i - 1, fillerItem);
            }
        }

        for (int i = 1; i <= size / 9 - 2; i++) {
            if (inventory.getItem(i * 9) == null) {
                inventory.setItem(i * 9, fillerItem);
                inventory.setItem(i * 9 + 8, fillerItem);
            }
        }
        inventory.setItem(0, fillerItem);
        inventory.setItem(8, fillerItem);
        inventory.setItem(size - 9, fillerItem);
        inventory.setItem(size - 1, fillerItem);
    }


    private void fill(Inventory inventory) {
        int size = inventory.getSize();

        for (int pos = 0; pos < size; pos++) {
            if (inventory.getItem(pos) == null)
                inventory.setItem(pos, fillerType);
        }
    }


    private ItemStack createItemStack(Player player, Button button) {
        ItemStack item = button.getButtonItem(player);

        if (item.getType() != Material.LEGACY_SKULL) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                meta.setDisplayName(meta.getDisplayName() + "§b§c§d§e");
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public void openMenu(final Player player) {
        this.buttons = this.getButtons(player);

        Menu previousMenu = Menu.currentlyOpenedMenus.get(player.getName());
        Inventory inventory = null;
        int size = this.getSize() == -1 ? this.size(this.buttons) : this.getSize();
        if (getFilter() != null) {
            this.filter = getFilter();
        }
        this.fixedPositions = getFixedPositions();
        this.resetCursor = resetCursor();

        boolean update = false;
        String title = CC.color(this.getTitle(player));

        if (title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (player.getOpenInventory() != null) {
            if (previousMenu == null) {
                player.closeInventory();
            } else {
                if (resetCursor) {
                    previousMenu.setClosedByMenu(true);
                    player.closeInventory();
                } else {
                    previousMenu.setClosedByMenu(true);
                    inventory = player.getOpenInventory().getTopInventory();
                    update = true;
                    player.updateInventory();
                }
            }
        }

        if (inventory == null) {
            inventory = Bukkit.createInventory(player, size, title);
        }

        inventory.setContents(new ItemStack[inventory.getSize()]);

        currentlyOpenedMenus.put(player.getName(), this);

        if (fixedPositions) {
            Map<Integer, Button> modifiedButtons = new HashMap<>();
            for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
                int slot = buttonEntry.getKey();
                if (filter != Filters.NONE && (slot % 9 == 0 || slot % 9 == 8)) {
                    slot += 2;
                }
                modifiedButtons.put(slot, buttonEntry.getValue());
                inventory.setItem(slot, createItemStack(player, buttonEntry.getValue()));
            }
            this.buttons = modifiedButtons;
        } else {
            for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
                inventory.setItem(buttonEntry.getKey(), createItemStack(player, buttonEntry.getValue()));
            }
            this.buttons = getButtons();
        }

        switch (filter) {
            case BORDER:
                fillBorder(inventory);
                break;
            case FILL:
                fill(inventory);
                break;
        }

        if (update) {
            player.updateInventory();
        } else {
            player.openInventory(inventory);
        }

        this.onOpen(player);
        this.setClosedByMenu(false);

    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;

        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((highest + 1) / 9D) * 9D);
    }

    public int getSize() {
        return -1;
    }

    public Filters getFilter() {
        return Filters.NONE;
    }

    public boolean getFixedPositions() {
        return true;
    }

    public boolean resetCursor() {
        return true;
    }

    public int getSlot(int x, int y) {
        return ((9 * y) + x);
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

}