package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.menu.KitsManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitDeleteButton extends Button {
    private final Kit kit;

    public KitDeleteButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.delete();
        new KitsManagementMenu().open(player);
        player.sendMessage(CC.success("Deleted Kit"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.RED_DYE).name("&cDelete kit").build();
    }
}
