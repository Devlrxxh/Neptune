package dev.lrxh.neptune.feature.leaderboard.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.feature.leaderboard.menu.button.LeaderboardSwitchButton;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
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
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        KitService.get().kits.forEach(kit ->
                buttons.add(new DisplayButton(kit.getSlot(), getButtonItem(player, kit)))
        );

        for (LeaderboardType type : LeaderboardType.values()) {
            buttons.add(createSwitchButton(type));
        }
        return buttons;
    }

    private LeaderboardSwitchButton createSwitchButton(LeaderboardType type) {
        boolean isCurrentType = type == leaderboardType;
        String state = isCurrentType ? "ENABLED" : "DISABLED";
        String configKey = "LEADERBOARD_TYPES_" + type.getConfigName();

        return new LeaderboardSwitchButton(
                MenusLocale.valueOf(configKey + "_SLOT").getInt(),
                type,
                MenusLocale.valueOf(configKey + "_" + state + "_NAME").getString(),
                MenusLocale.valueOf(configKey + "_" + state + "_LORE").getStringList(),
                Material.valueOf(MenusLocale.valueOf(configKey + "_" + state + "_MATERIAL").getString())
        );
    }

    public ItemStack getButtonItem(Player player, Kit kit) {
        List<String> lore = buildLoreForKit(kit);

        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.LEADERBOARD_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore, player)
                .build();
    }

    private List<String> buildLoreForKit(Kit kit) {
        List<PlayerEntry> leaderboard = LeaderboardService.get().getPlayerEntries(kit, leaderboardType);

        return MenusLocale.LEADERBOARD_LORE.getStringList().stream()
                .map(line -> replacePlaceholders(line, kit, leaderboard))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    private String replacePlaceholders(String line, Kit kit, List<PlayerEntry> leaderboard) {
        String result = line;

        for (int i = 1; i <= 10; i++) {
            PlayerEntry entry = getPlayerEntry(kit, leaderboard, i);

            String playerName = entry != null ? entry.getUsername() : "???";
            String value = entry != null ? String.valueOf(entry.getValue()) : "???";

            result = result.replaceAll("<player_" + i + ">", playerName)
                    .replaceAll("<value_" + i + ">", value);
        }

        return result;
    }

    private PlayerEntry getPlayerEntry(Kit kit, List<PlayerEntry> leaderboard, int position) {
        return position <= leaderboard.size()
                ? LeaderboardService.get().getLeaderboardSlot(kit, leaderboardType, position)
                : null;
    }
}