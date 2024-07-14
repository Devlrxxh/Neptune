package dev.lrxh.neptune.cosmetics.menu.killMessages;

import dev.lrxh.neptune.configs.impl.CosmeticsLocale;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class KillMessageButton extends Button {
    private final KillMessagePackage killMessagePackage;

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getKillMessagePackage().equals(killMessagePackage);
        List<String> lore;

        if (player.hasPermission(killMessagePackage.permission())) {
            lore = selected ? MenusLocale.KILL_EFFECTS_SELECTED_LORE.getStringList() : MenusLocale.KILL_EFFECTS_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.KILL_EFFECTS_NO_PERMISSION_LORE.getStringList();
        }

        return new ItemBuilder(killMessagePackage.getMaterial()).name(killMessagePackage.getDisplayName()
                        .replace("<selected>", selected ? CosmeticsLocale.SELECTED_DISPLAY_NAME.getString() : ""))
                .lore(lore)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (!player.hasPermission(killMessagePackage.permission())) return;
        plugin.getProfileManager().getByUUID(player.getUniqueId()).getSettingData().setKillMessagePackage(killMessagePackage);
    }
}
