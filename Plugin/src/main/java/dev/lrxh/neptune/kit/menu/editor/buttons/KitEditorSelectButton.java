package dev.lrxh.neptune.kit.menu.editor.buttons;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitEditorSelectButton extends Button {
    private final Kit kit;

    public KitEditorSelectButton(int slot, Kit kit) {
        super(slot);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);

        MessagesLocale.KIT_EDITOR_START.send(player.getUniqueId(), new Replacement("<kit>", kit.getDisplayName()));
        player.closeInventory();

        profile.getGameData().setKitEditor(kit);
        profile.setState(ProfileState.IN_KIT_EDITOR);

        kit.giveLoadout(player.getUniqueId());

        player.updateInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.KIT_EDITOR_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.KIT_EDITOR_SELECT_LORE.getStringList(), player)
                .build();
    }
}