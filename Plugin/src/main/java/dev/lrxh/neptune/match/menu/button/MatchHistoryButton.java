package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@AllArgsConstructor
public class MatchHistoryButton extends Button {
    private MatchHistory matchHistory;

    @Override
    public ItemStack getButtonItem(Player player) {
        Kit kit = KitManager.get().getKitByDisplay(matchHistory.getKitName());
        if (kit == null) {
            return new ItemBuilder(Material.COMPASS)
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString()
                            .replace("<kit>", matchHistory.getKitName())
                            .replace("<won>", matchHistory.isWon() ? "&aWon" : "&cLost")
                            .replace("<winner>", matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName())
                            .replace("<loser>", !matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()))
                    .lore(ItemUtils.getLore(MenusLocale.MATCH_HISTORY_LORE.getStringList(),
                            new Replacement("<arena>", matchHistory.getArenaName()),
                            new Replacement("<kit>", matchHistory.getKitName()),
                            new Replacement("<winner>", matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()),
                            new Replacement("<loser>", !matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()),
                            new Replacement("<date>", matchHistory.getDate())), player)
                    .clearFlags()
                    .build();
        } else {
            return new ItemBuilder(kit.getIcon())
                    .name(MenusLocale.MATCH_HISTORY_ITEM_NAME.getString()
                            .replace("<kit>", matchHistory.getKitName())
                            .replace("<won>", matchHistory.isWon() ? "&aWon" : "&cLost")
                            .replace("<winner>", matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName())
                            .replace("<loser>", !matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()))
                    .lore(ItemUtils.getLore(MenusLocale.MATCH_HISTORY_LORE.getStringList(),
                            new Replacement("<arena>", matchHistory.getArenaName()),
                            new Replacement("<kit>", matchHistory.getKitName()),
                            new Replacement("<winner>", matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()),
                            new Replacement("<loser>", !matchHistory.isWon() ? player.getName() : matchHistory.getOpponentName()),
                            new Replacement("<date>", matchHistory.getDate())), player)
                    .clearFlags()
                    .build();
        }

    }
}
