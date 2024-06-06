package dev.lrxh.neptune.providers.menus.stats;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class StatButton extends Button {
    private final Kit kit;
    private final Player target;

    @Override
    public ItemStack getButtonItem(Player player) {
        KitData data = Neptune.get().getProfileManager().getByUUID(target.getUniqueId()).getGameData().getKitData().get(kit);


        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(ItemUtils.getLore(MenusLocale.STAT_LORE.getStringList(), new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<wins>", String.valueOf(data.getWins())),
                        new Replacement("<losses>", String.valueOf(data.getLosses())),
                        new Replacement("<win_streak_current>", String.valueOf(data.getCurrentStreak())),
                        new Replacement("<win_streak_best>", String.valueOf(data.getBestStreak())),
                        new Replacement("<kill_death_ratio>", String.valueOf(data.getKdr()))))
                .clearFlags()
                .build();
    }
}