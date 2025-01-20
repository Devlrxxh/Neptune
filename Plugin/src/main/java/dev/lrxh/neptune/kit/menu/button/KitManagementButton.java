package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class KitManagementButton extends Button {
    private KitRule rule;
    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.is(rule) ? Material.GREEN_WOOL : Material.RED_WOOL)
                .name(CC.color(kit.is(rule) ? "&a" + rule.getName() : "&c" + rule.getName()))
                .lore(Arrays.asList(" ", CC.color("&7" + rule.getDescription()), " ", CC.color(kit.is(rule) ? "&cClick to disable." : "&aClick to enable.")), player)
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        kit.set(rule);
        KitManager.get().kits.add(kit);
        KitManager.get().saveKits();
        new KitManagementMenu(kit).openMenu(player.getUniqueId());

        player.sendMessage(CC.color("&7" + rule.getName() + " has been set to " + (kit.is(rule) ? "&aenabled" : "&cdisabled")));
    }
}
