package dev.lrxh.neptune.providers.menus.kitEditor;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class KitEditorSelectButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {

        return new ItemBuilder(kit.getIcon().getType()).name(MenusLocale.KIT_EDITOR_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.KIT_EDITOR_SELECT_LORE.getStringList())
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());

        MessagesLocale.KIT_EDITOR_START.send(player.getUniqueId());
        player.closeInventory();

        profile.setKitEditor(kit);
        profile.setState(ProfileState.IN_KIT_EDITOR);

        PlayerUtil.giveKit(player.getUniqueId(), kit);

        player.updateInventory();
    }
}