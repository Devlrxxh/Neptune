package dev.lrxh.neptune.kit.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.Rules;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class KitManagementButton extends Button {
    private Rules rule;
    private Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(rule.enabled(kit) ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                .name(CC.color(rule.enabled(kit) ? "&a" + rule.getName() : "&c" + rule.getName()))
                .lore(Arrays.asList(" ", CC.color("&7" + rule.getDescription()), " ", CC.color(rule.enabled(kit) ? "&cClick to disable." : "&aClick to enable.")))
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        rule.execute(kit, !rule.enabled(kit));
        plugin.getKitManager().kits.add(kit);
        plugin.getKitManager().saveKits();
        new KitManagementMenu(kit).openMenu(player);

        player.sendMessage(CC.color("&7" + rule.getName() + " has been set to " + (rule.enabled(kit) ? "&aenabled" : "&cdisabled")));
    }
}
