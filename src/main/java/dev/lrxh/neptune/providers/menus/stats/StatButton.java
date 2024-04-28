package dev.lrxh.neptune.providers.menus.stats;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StatButton extends Button {
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        KitData data = Neptune.get().getProfileManager().getByUUID(player.getUniqueId()).getData().getKitData().get(kit);

        MenusLocale.STAT_LORE.getStringList().forEach(line -> {
            line = line.replaceAll("<wins_unranked>", String.valueOf(data.getUnrankedWins()));
            line = line.replaceAll("<losses_unranked>", String.valueOf(data.getUnrankedLosses()));

            line = line.replaceAll("<wins_ranked>", String.valueOf(data.getRankedWins()));
            line = line.replaceAll("<losses_ranked>", String.valueOf(data.getRankedLosses()));

            line = line.replaceAll("<win_streak_unranked_current>", String.valueOf(data.getCurrentUnrankedStreak()));
            line = line.replaceAll("<win_streak_ranked_current>", String.valueOf(data.getCurrentRankedStreak()));

            line = line.replaceAll("<win_streak_unranked_best>", String.valueOf(data.getUnrankedBestStreak()));
            line = line.replaceAll("<win_streak_ranked_best>", String.valueOf(data.getRankedBestStreak()));

            lore.add(line);
        });

        return new ItemBuilder(kit.getIcon().getType())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore)
                .build();
    }
}