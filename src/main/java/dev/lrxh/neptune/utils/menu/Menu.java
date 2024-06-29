package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public abstract class Menu {
    public static Neptune plugin = Neptune.get();
    public Map<Integer, Button> buttons = new TreeMap<>();

    public static void remove(UUID playerUUID) {
        plugin.getMenuManager().openedMenus.remove(playerUUID);
    }

    public String getUUID() {
        return buttons.keySet().toString();
    }

    public void openMenu(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Inventory inventory = Bukkit.createInventory(player, getSize(), Component.text(CC.color(getTitle(player))));
        inventory.setContents(new ItemStack[inventory.getSize()]);

        buttons.clear();
        buttons.putAll(getButtons(player));

        if (getFilter() != Filter.NONE) {
            TreeMap<Integer, Button> modifiedButtons = new TreeMap<>();
            for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
                int slot = buttonEntry.getKey();
                if ((slot % 9 == 0 || slot % 9 == 8) && !(buttonEntry.getValue() instanceof PageButton && buttonEntry.getValue().isDisplay())) {
                    while (buttons.get(slot) != null) {
                        slot += 2;
                    }
                }

                modifiedButtons.put(slot, buttonEntry.getValue());
            }
            this.buttons = modifiedButtons;
        }

        switch (getFilter()) {
            case BORDER:
                addBorder(inventory);
                break;
            case FILL:
                addFilling(inventory);
                break;
            case NONE:
                break;
        }

        for (Map.Entry<Integer, Button> buttonEntry : this.buttons.entrySet()) {
            set(inventory, buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }

        player.openInventory(inventory);
        player.updateInventory();
        changeMenu(playerUUID);
    }

    private void set(Inventory inventory, int slot, ItemStack itemStack) {
        if (slot < inventory.getSize()) {
            inventory.setItem(slot, itemStack);
        }
    }

    public void changeMenu(UUID playerUUID) {
        plugin.getMenuManager().openedMenus.remove(playerUUID);
        plugin.getMenuManager().openedMenus.put(playerUUID, this);
    }

    private void addBorder(Inventory inventory) {
        int size = inventory.getSize();

        if (size < 9) return;

        for (int i = 1; i <= 7 && size >= 18; i++) {
            Button button = new Button() {
                @Override
                public boolean isDisplay() {
                    return true;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return getFilterItem();
                }
            };

            buttons.put(i, button);
        }
    }

    private void addFilling(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (buttons.get(i) == null) {
                Button button = new Button() {
                    @Override
                    public boolean isDisplay() {
                        return true;
                    }

                    @Override
                    public ItemStack getButtonItem(Player player) {
                        return getFilterItem();
                    }
                };

                buttons.put(i, button);
            }
        }
    }

    public void update() {
        Map<UUID, Menu> openedMenusCopy = new HashMap<>(plugin.getMenuManager().openedMenus);
        for (Map.Entry<UUID, Menu> entry : openedMenusCopy.entrySet()) {
            if (entry.getValue().getUUID().equals(getUUID())) {
                UUID uuid = entry.getKey();
                openMenu(uuid);
            }
        }
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public int getSize() {
        return 27;
    }

    public ItemStack getFilterItem() {
        return new ItemBuilder(MenusLocale.FILTER_MATERIAL.getString())
                .name(MenusLocale.FILTER_NAME.getString()).amount(1).build();
    }

    public Filter getFilter() {
        return Filter.NONE;
    }

    public boolean isUpdateOnClick() {
        return false;
    }
}