package dev.lrxh.neptune.providers.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class KitSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private int rounds;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .amount(kit.getPlaying())
                .lore(MenusLocale.DUEL_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getByUUID(receiver);
        if (profile == null) return;
        Player receiverPlayer = Bukkit.getPlayer(receiver);
        if (receiverPlayer == null) {
            player.closeInventory();
            return;
        }

        DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, plugin.getArenaManager().getRandomArena(kit), rounds);
        MessagesLocale.DUEL_REQUEST_SENDER.send(player.getUniqueId(),
                new Replacement("<receiver>", receiverPlayer.getName()),
                new Replacement("<kit>", kit.getDisplayName()),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()));

        player.closeInventory();
        profile.sendDuel(duelRequest);
    }
}