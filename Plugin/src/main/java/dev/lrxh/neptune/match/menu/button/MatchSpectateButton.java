package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MatchSpectateButton extends Button {
    private final SoloFightMatch match;

    public MatchSpectateButton(int slot, SoloFightMatch match) {
        super(slot);
        this.match = match;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.chat("/spec " + match.getParticipantA().getNameUnColored());
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(match.kit.getIcon())
                .name(MenusLocale.MATCH_LIST_ITEM_NAME.getString()
                        .replace("<playerRed_name>", match.getParticipantA().getNameUnColored())
                        .replace("<playerBlue_name>", match.getParticipantB().getNameUnColored()))
                .lore(ItemUtils.getLore(MenusLocale.MATCH_LIST_ITEM_LORE.getStringList(),
                        new Replacement("<arena>", match.getArena().getDisplayName()),
                        new Replacement("<kit>", match.getKit().getDisplayName())), player)
                .clearFlags()
                .build();
    }
}
