package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RoundSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private final boolean party;
    private final int round;

    public RoundSelectButton(int slot, Kit kit, UUID receiver, boolean party, int round) {
        super(slot);
        this.kit = kit;
        this.receiver = receiver;
        this.party = party;
        this.round = round;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.MAP)
                .name(MenusLocale.ROUNDS_ITEM_NAME.getString().replace("<rounds>", String.valueOf(round)))
                .lore(MenusLocale.ROUNDS_LORE.getStringList(), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(receiver);
        if (profile == null) return;
        DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, kit.getRandomArena(), party, round);
        profile.sendDuel(duelRequest);
        player.closeInventory();
    }
}
