package dev.lrxh.neptune.game.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MatchSpectateTeleportButton extends Button {
    Participant participant;

    public MatchSpectateTeleportButton(int slot, Participant player) {
        super(slot);
        this.participant = player;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.teleport(participant.getPlayer().getLocation());
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder("PLAYER_HEAD", participant.getPlayerUUID())
                .name(MenusLocale.MATCH_SPECTATE_NAME.getString().replace("<player>", participant.getName()))
                .lore(ItemUtils.getLore(MenusLocale.MATCH_SPECTATE_LORE.getStringList(),
                        new Replacement("<player>", participant.getName())))
                .build();
    }
}
