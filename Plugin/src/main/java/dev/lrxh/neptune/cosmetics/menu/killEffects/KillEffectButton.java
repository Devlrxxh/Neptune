package dev.lrxh.neptune.cosmetics.menu.killEffects;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class KillEffectButton extends Button {
    private final KillEffect killEffect;

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getKillEffect().equals(killEffect);
        List<String> lore;

        if (player.hasPermission(killEffect.permission())) {
            lore = selected ? MenusLocale.KILL_EFFECTS_SELECTED_LORE.getStringList() : MenusLocale.KILL_EFFECTS_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.KILL_EFFECTS_NO_PERMISSION_LORE.getStringList();
        }

        return new ItemBuilder(killEffect.getMaterial())
                .name(selected ? MenusLocale.KILL_EFFECTS_NAME_SELECTED.getString().replace("<displayName>", killEffect.getDisplayName()) : MenusLocale.KILL_EFFECTS_NAME_NOT_SELECTED.getString().replace("<displayName>", killEffect.getDisplayName()))
                .lore(lore, player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (!player.hasPermission(killEffect.permission())) return;
        plugin.getProfileManager().getByUUID(player.getUniqueId()).getSettingData().setKillEffect(killEffect);
    }
}
