package dev.lrxh.neptune.providers.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.providers.duel.DuelRequest;
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

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon().getType()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .amount(kit.getPlaying())
                .lore(MenusLocale.DUEL_LORE.getStringList())
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getByUUID(receiver);
        if(profile == null) return;

        profile.sendDuel(new DuelRequest(player.getUniqueId(), kit, plugin.getArenaManager().getRandomArena(kit)));
    }
}