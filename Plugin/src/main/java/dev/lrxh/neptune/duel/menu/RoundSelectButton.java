package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class RoundSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private final boolean party;
    private int round;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.MAP)
                .name(MenusLocale.ROUNDS_ITEM_NAME.getString().replace("<rounds>", String.valueOf(round)))
                .lore(MenusLocale.ROUNDS_LORE.getStringList(), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        Profile profile = plugin.getAPI().getProfile(receiver);
        if (profile == null) return;
        DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, kit.getRandomArena(), party, round, plugin);
        profile.sendDuel(duelRequest);
        player.closeInventory();
    }
}
