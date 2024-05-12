package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MatchHistoryButton extends Button {
    private MatchHistory matchHistory;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        MenusLocale.MATCH_HISTORY_LORE.getStringList().forEach(line -> {
            line = line.replaceAll("<arena>", matchHistory.getArenaName());
            line = line.replaceAll("<kit>", matchHistory.getKitName());
            line = line.replaceAll("<winner>", matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName());
            line = line.replaceAll("<loser>", !matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName());

            lore.add(line);
        });

        Kit kit = plugin.getKitManager().getKitByDisplay(matchHistory.getKitName());
        if (kit == null) {
            return new ItemBuilder(Material.COMPASS)
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString().replace("<kit>",
                            matchHistory.getKitName()).replace("<won>", matchHistory.isWon() ? "&aWon" : "&cLost"))
                    .lore(lore)
                    .clearFlags()
                    .build();
        } else {
            return new ItemBuilder(kit.getIcon())
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString().replace("<kit>",
                            matchHistory.getKitName()).replace("<won>", matchHistory.isWon() ? "&aWon" : "&cLost"))
                    .lore(lore)
                    .clearFlags()
                    .build();
        }

    }
}
