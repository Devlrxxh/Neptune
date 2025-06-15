package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RoundSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private final int round;

    public RoundSelectButton(int slot, Kit kit, UUID receiver, int round) {
        super(slot);
        this.kit = kit;
        this.receiver = receiver;
        this.round = round;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.MAP)
                .name(MenusLocale.ROUNDS_ITEM_NAME.getString().replace("<rounds>", String.valueOf(round)))
                .lore(MenusLocale.ROUNDS_LORE.getStringList(), player)

                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(receiver);
        if (profile == null) return;
        Arena arena = kit.getRandomArena();
        if (arena == null) {
            player.sendMessage(CC.error("No arena found, please contact and admin"));
            return;
        }
        DuelRequest duelRequest = new DuelRequest(player.getUniqueId(), kit, arena, false, round);
        profile.sendDuel(duelRequest);
        player.closeInventory();
    }
}
