package dev.lrxh.neptune.cosmetics.menu.killMessages;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
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
        Profile profile = API.getProfile(player);
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getKillMessagePackage().equals(killMessagePackage);
        List<String> lore;

        if (player.hasPermission(killMessagePackage.permission())) {
            lore = selected ? MenusLocale.KILL_MESSAGES_SELECTED_LORE.getStringList() : MenusLocale.KILL_MESSAGES_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.KILL_MESSAGES_NO_PERMISSION_LORE.getStringList();
        }

        return new ItemBuilder(killMessagePackage.getMaterial())
                .name(selected ? MenusLocale.KILL_MESSAGES_NAME_SELECTED.getString().replace("<displayName>", killMessagePackage.getDisplayName()) : MenusLocale.KILL_MESSAGES_NAME_NOT_SELECTED.getString().replace("<displayName>", killMessagePackage.getDisplayName()))
                .lore(ItemUtils.getLore(lore,
                        new Replacement("<description>", killMessagePackage.getDescription()),
                        new Replacement("<messages>", ItemUtils.getLore(killMessagePackage.getMessages(), new Replacement("<player>", player.getName()), new Replacement("<killer>", player.getName())))), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (!player.hasPermission(killMessagePackage.permission())) return;
        API.getProfile(player).getSettingData().setKillMessagePackage(killMessagePackage);
    }
}
