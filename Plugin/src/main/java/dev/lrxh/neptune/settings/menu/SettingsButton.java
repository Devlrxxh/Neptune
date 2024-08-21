package dev.lrxh.neptune.settings.menu;

import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.settings.Setting;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
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
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        return new ItemBuilder(setting.getMaterial(), player.getUniqueId())
                .name(setting.getDisplayName())
                .lore(ItemUtils.getLore(setting.toggled(player) ? setting.getEnabledLore() : setting.getDisabledLore(),
                        new Replacement("<ping>", profile.getSettingData().getMaxPing()),
                        new Replacement("<kill-effect>", profile.getSettingData().getKillEffect().getDisplayName()),
                        new Replacement("<kill-message>", profile.getSettingData().getKillMessagePackage().getDisplayName())), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        setting.execute(player, clickType);
    }
}
