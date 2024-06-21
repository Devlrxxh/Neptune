package dev.lrxh.neptune.utils.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public abstract class Menu {
    private static final List<UUID> viewers = new ArrayList<>();
    public static Neptune plugin = Neptune.get();
    public WeakHashMap<Integer, Button> buttons = new WeakHashMap<>();
    private Inventory inventory;

    public static void remove(UUID playerUUID) {
        plugin.getMenuManager().openedMenus.remove(playerUUID);
        viewers.remove(playerUUID);
    }

    public void openMenu(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        //Create the inventory
        Inventory inventory = Bukkit.createInventory(player, getSize(), Component.text(CC.color(getTitle(player))));
        inventory.setContents(new ItemStack[inventory.getSize()]);

        //Put all the buttons into a list
        buttons.putAll(getButtons(player));

        switch (getFilter()) {
            case BRODER:
                addBorder(inventory);
                break;
            case FILL:
                addFilling(inventory);
            case NONE:
                break;
        }

        //Create the buttons in the menu
        for (Map.Entry<Integer, Button> button : buttons.entrySet()) {
            inventory.setItem(button.getKey(), button.getValue().getButtonItem(player));
        }
        this.inventory = inventory;

        player.openInventory(inventory);
        player.updateInventory();
        changeMenu(playerUUID);
    }

    public void changeMenu(UUID playerUUID) {
        viewers.remove(playerUUID);
        plugin.getMenuManager().openedMenus.remove(playerUUID);
        plugin.getMenuManager().openedMenus.put(playerUUID, this);
        viewers.add(playerUUID);
    }

    private void addBorder(Inventory inventory) {
        int size = inventory.getSize();

        if (size < 9) return;

        ItemStack fillerItem = getFilterItem();

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


            if (inventory.getItem(i) == null) {
                inventory.setItem(i, fillerItem);
                inventory.setItem(size - i - 1, fillerItem);
            }
        }

        for (int i = 1; i <= 2 && size >= 18; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i * 9, fillerItem);
                inventory.setItem(i * 9 + 8, fillerItem);
            }
        }
        inventory.setItem(0, fillerItem);
        inventory.setItem(8, fillerItem);
        inventory.setItem(size - 9, fillerItem);
        inventory.setItem(size - 1, fillerItem);
    }

    private void addFilling(Inventory inventory) {
        int size = inventory.getSize();
        for (int pos = 0; pos < size; pos++) {
            if (buttons.get(pos) == null) {
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

                buttons.put(pos, button);
            }
        }
    }

    public void update() {
        for (UUID uuid : viewers) {
            openMenu(uuid);
            changeMenu(uuid);
        }
    }

    public abstract String getTitle(Player player);

    public abstract Map<Integer, Button> getButtons(Player player);

    public int getSize() {
        return 27;
    }

    public ItemStack getFilterItem() {
        return new ItemBuilder(
                XMaterial.matchXMaterial(MenusLocale.FILTER_MATERIAL.getString()).get().parseItem())
                .name(MenusLocale.FILTER_NAME.getString()).amount(1).build();
    }

    public Filter getFilter() {
        return Filter.BRODER;
    }

    public boolean isUpdateOnClick() {
        return false;
    }
}
