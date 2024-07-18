package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class RoundSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private final boolean test;
    private int round;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.MAP)
                .name(MenusLocale.ROUNDS_ITEM_NAME.getString().replace("<rounds>", String.valueOf(round)))
                .lore(MenusLocale.ROUNDS_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getByUUID(receiver);
        if (profile == null) return;
        Player receiverPlayer = Bukkit.getPlayer(receiver);
        if (receiverPlayer == null) {
            player.closeInventory();
            return;
        }

        DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, kit.getRandomArena(), test, round);
        MessagesLocale.DUEL_REQUEST_SENDER.send(player.getUniqueId(),
                new Replacement("<receiver>", receiverPlayer.getName()),
                new Replacement("<kit>", kit.getDisplayName()),
                new Replacement("<rounds>", String.valueOf(round)),
                new Replacement("<arena>", duelRequest.getArena().getDisplayName()));

        player.closeInventory();
        profile.sendDuel(duelRequest, player.getUniqueId());
    }
}
