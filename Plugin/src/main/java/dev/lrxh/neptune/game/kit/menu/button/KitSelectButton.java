package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSelectButton extends Button {
    private final Kit kit;

    public KitSelectButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitManagementMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon()).name("&f" + kit.getName() + " &7(" + kit.getDisplayName() + "&7)").lore("&7Press to manage kit").build();
    }
}
