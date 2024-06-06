package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class MatchSpectateButton extends Button {
    private final SoloFightMatch match;

    @Override
    public ItemStack getButtonItem(Player player) {

        return new ItemBuilder(match.kit.getIcon())
                .name(MenusLocale.MATCH_LIST_ITEM_NAME.getString()
                        .replace("<playerRed_name>", match.getParticipantA().getNameUnColored())
                        .replace("<playerBlue_name>", match.getParticipantB().getNameUnColored()))
                .lore(ItemUtils.getLore(MenusLocale.MATCH_LIST_ITEM_LORE.getStringList(),
                        new Replacement("<arena>", match.getArena().getDisplayName())))
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.chat("/spec " + match.getParticipantA().getNameUnColored());
    }
}
