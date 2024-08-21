package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class KitSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private boolean party;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.DUEL_LORE.getStringList(), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (party) {
            Profile profile = plugin.getProfileManager().getByUUID(receiver);
            if (profile == null) return;
            DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, kit.getRandomArena(), party, 1, plugin);
            profile.sendDuel(duelRequest);
            player.closeInventory();
        } else {
            new RoundsSelectMenu(kit, receiver, false).openMenu(player.getUniqueId());
        }
    }
}