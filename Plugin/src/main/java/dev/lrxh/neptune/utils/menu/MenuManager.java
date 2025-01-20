package dev.lrxh.neptune.utils.menu;

import java.util.UUID;
import java.util.WeakHashMap;

public class MenuManager {
    private static MenuManager instance;
    public WeakHashMap<UUID, Menu> openedMenus;

    public MenuManager() {
        this.openedMenus = new WeakHashMap<>();
    }

    public static MenuManager get() {
        if (instance == null) instance = new MenuManager();

        return instance;
    }
}
