package dev.lrxh.neptune.utils.menu;

import java.util.UUID;
import java.util.WeakHashMap;

public class MenuManager {
    public WeakHashMap<UUID, Menu> openedMenus = new WeakHashMap<>();
}
