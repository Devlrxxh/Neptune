package dev.lrxh.neptune.feature.leaderboard.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.entry.player.PlayerLeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.menu.button.LeaderboardSwitchButton;
import dev.lrxh.neptune.feature.leaderboard.metadata.LeaderboardType;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardMenu extends Menu {

    private final LeaderboardType leaderboardType;

    public LeaderboardMenu(LeaderboardType leaderboardType) {
        super(
                MenusLocale.valueOf("LEADERBOARD_TYPES_" + leaderboardType.getConfigName() + "_TITLE").getString(),
                MenusLocale.LEADERBOARD_SIZE.getInt(),
                Filter.valueOf(MenusLocale.LEADERBOARD_FILTER.getString())
        );
        this.leaderboardType = leaderboardType;
        setUpdateEveryTick(true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            buttons.add(new DisplayButton(kit.getSlot(), buildKitItem(player, kit)));
        }

        for (LeaderboardType type : LeaderboardType.values()) {
            buttons.add(buildSwitchButton(type));
        }

        return buttons;
    }

    private LeaderboardSwitchButton buildSwitchButton(LeaderboardType type) {
        boolean isSelected = (type == leaderboardType);
        String state = isSelected ? "ENABLED" : "DISABLED";
        String baseKey = "LEADERBOARD_TYPES_" + type.getConfigName();

        return new LeaderboardSwitchButton(
                MenusLocale.valueOf(baseKey + "_SLOT").getInt(),
                type,
                MenusLocale.valueOf(baseKey + "_" + state + "_NAME").getString(),
                MenusLocale.valueOf(baseKey + "_" + state + "_LORE").getStringList(),
                Material.valueOf(MenusLocale.valueOf(baseKey + "_" + state + "_MATERIAL").getString())
        );
    }

    private ItemStack buildKitItem(Player player, Kit kit) {
        List<String> lore = buildKitLore(kit);
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.LEADERBOARD_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore, player)
                .build();
    }

    private List<String> buildKitLore(Kit kit) {
        List<PlayerLeaderboardEntry> leaderboard = LeaderboardService.get().getPlayerEntries(kit, leaderboardType);
        List<String> lore = new ArrayList<>();

        for (String template : MenusLocale.LEADERBOARD_LORE.getStringList()) {
            lore.add(replaceLeaderboardPlaceholders(template, kit, leaderboard));
        }

        return lore;
    }

    private String replaceLeaderboardPlaceholders(String template, Kit kit, List<PlayerLeaderboardEntry> leaderboard) {
        String result = template;

        for (int i = 1; i <= 10; i++) {
            PlayerLeaderboardEntry entry = getEntryAtPosition(kit, leaderboard, i);
            String playerName = entry != null ? entry.username() : "???";
            String value = entry != null ? String.valueOf(entry.value()) : "???";

            result = result
                    .replace("<player_" + i + ">", playerName)
                    .replace("<value_" + i + ">", value);
        }

        return result;
    }

    private PlayerLeaderboardEntry getEntryAtPosition(Kit kit, List<PlayerLeaderboardEntry> leaderboard, int position) {
        return position <= leaderboard.size()
                ? LeaderboardService.get().getLeaderboardSlot(kit, leaderboardType, position)
                : null;
    }
}