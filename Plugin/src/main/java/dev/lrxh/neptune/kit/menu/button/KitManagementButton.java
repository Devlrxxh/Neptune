package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitManagementButton extends Button {
    private final KitRule rule;
    private final Kit kit;

    public KitManagementButton(int slot, KitRule rule, Kit kit) {
        super(slot);
        this.rule = rule;
        this.kit = kit;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.is(rule) ? Material.GREEN_WOOL : Material.RED_WOOL)
                .name(CC.color(kit.is(rule) ? "&a" + rule.getName() : "&c" + rule.getName()))
                .lore(Arrays.asList(" ", CC.color("&7" + rule.getDescription()), " ", CC.color(kit.is(rule) ? "&cClick to disable." : "&aClick to enable.")), player)
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.set(rule);
        KitManager.get().kits.add(kit);
        KitManager.get().saveKits();
        new KitManagementMenu(kit).open(player);

        player.sendMessage(CC.color("&7" + rule.getName() + " has been set to " + (kit.is(rule) ? "&aenabled" : "&cdisabled")));
    }
}
