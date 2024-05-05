package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.impl.OneVersusOneMatch;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MatchSpectateButton extends Button {
    private final OneVersusOneMatch match;

    @Override
    public ItemStack getButtonItem(Player player) {

        List<String> lore = new ArrayList<>();

        MenusLocale.MATCH_LIST_ITEM_LORE.getStringList().forEach(line -> {
            line = line.replaceAll("<arena>", match.getArena().getDisplayName());
            line = line.replaceAll("<kit>", match.getKit().getDisplayName());
            lore.add(line);
        });

        return new ItemBuilder(match.kit.getIcon())
                .name(MenusLocale.MATCH_LIST_ITEM_NAME.getString()
                        .replace("<playerRed_name>", match.getParticipantA().getNameUnColored())
                        .replace("<playerBlue_name>", match.getParticipantB().getNameUnColored()))
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.chat("/spec " + match.getParticipantA().getNameUnColored());
    }
}
