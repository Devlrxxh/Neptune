package dev.lrxh.neptune.settings.menu;

import dev.lrxh.neptune.settings.Setting;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SettingsButton extends Button {
    private Setting setting;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(setting.getMaterial(), player.getUniqueId())
                .name(setting.getDisplayName())
                .lore(setting.toggled(player) ? setting.getEnabledLore() : setting.getDisabledLore())
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        setting.execute(player);
    }
}
