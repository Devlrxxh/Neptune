package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.duel.DuelRequest;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KitSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private final boolean party;

    public KitSelectButton(int slot, Kit kit, UUID receiver, boolean party) {
        super(slot);
        this.kit = kit;
        this.receiver = receiver;
        this.party = party;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.DUEL_LORE.getStringList(), player)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (party) {
            Profile profile = API.getProfile(receiver);
            if (profile == null) return;
            DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, kit.getRandomArena(), party, 1);
            profile.sendDuel(duelRequest);
            player.closeInventory();
        } else {
            new RoundsSelectMenu(kit, receiver, false).open(player);
        }
    }
}