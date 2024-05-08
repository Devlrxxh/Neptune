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
    private final Player target;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();
        KitData data = Neptune.get().getProfileManager().getByUUID(target.getUniqueId()).getData().getKitData().get(kit);

        MenusLocale.STAT_LORE.getStringList().forEach(line -> {
            line = line.replaceAll("<wins>", String.valueOf(data.getWins()));
            line = line.replaceAll("<losses>", String.valueOf(data.getLosses()));

            line = line.replaceAll("<win_streak_current>", String.valueOf(data.getCurrentStreak()));
            line = line.replaceAll("<win_streak_best>", String.valueOf(data.getBestStreak()));
            line = line.replaceAll("<kill_death_ratio>", String.valueOf(data.getKdr()));


            lore.add(line);
        });

        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore)
                .clearFlags()
                .build();
    }
}