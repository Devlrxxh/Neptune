package dev.lrxh.api.features;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface IItemBrowserService {
    /**
     * Get all items for a section (e.g. "weapons", "tools")
     */
    List<Material> getItems(String section);

    /**
     * Open browser for a section
     */
    void openBrowser(Player player, String section, Consumer<Material> itemConsumer, Runnable returnConsumer);

    /**
     * Register new items for a section (for dynamic loading)
     */
    void registerSection(String section, List<String> materialNames);
}