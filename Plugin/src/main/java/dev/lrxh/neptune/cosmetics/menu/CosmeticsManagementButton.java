package dev.lrxh.neptune.cosmetics.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class CosmeticsManagementButton extends Button {
    private final String title;
    private final List<String> lore;
    private final Material material;
    private final Menu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = API.getProfile(player);
        return new ItemBuilder(material).name(title).lore(ItemUtils.getLore(lore, new Replacement("<kill-effect>", profile.getSettingData().getKillEffect().getDisplayName()), new Replacement("<kill-message>", profile.getSettingData().getKillMessagePackage().getDisplayName()
        )), player).clearFlags().build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        menu.openMenu(player.getUniqueId());
    }
}
