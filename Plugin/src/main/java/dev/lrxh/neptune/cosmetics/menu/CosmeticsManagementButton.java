package dev.lrxh.neptune.cosmetics.menu;

import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class CosmeticsManagementButton extends Button {
    private final String title;
    private final List<String> lore;
    private final Material material;
    private final Menu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(material).name(title).lore(lore).clearFlags().build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        menu.openMenu(player.getUniqueId());
    }
}
