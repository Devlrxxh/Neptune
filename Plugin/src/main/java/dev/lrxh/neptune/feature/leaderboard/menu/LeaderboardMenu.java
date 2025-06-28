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
import java.util.Arrays;
import java.util.List;

public class LeaderboardMenu extends Menu {
    private static final int MAX_LEADERBOARD_ENTRIES = 10;
    private static final String PLACEHOLDER = "???";

    private static final LeaderboardTypeConfig[] TYPE_CONFIGS = {
            new LeaderboardTypeConfig(LeaderboardType.WINS, "KILLS"),
            new LeaderboardTypeConfig(LeaderboardType.BEST_WIN_STREAK, "WIN_STREAK"),
            new LeaderboardTypeConfig(LeaderboardType.DEATHS, "DEATHS"),
            new LeaderboardTypeConfig(LeaderboardType.ELO, "ELO")
    };

    private final LeaderboardType leaderboardType;

    public LeaderboardMenu(LeaderboardType leaderboardType) {
        super(
                MenusLocale.LEADERBOARD_TITLE.getString().replace("<type>", leaderboardType.getName()),
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

        buttons.addAll(createLeaderboardSwitchButtons());

        return buttons;
    }

    private List<Button> createLeaderboardSwitchButtons() {
        return Arrays.stream(TYPE_CONFIGS)
                .map(this::createSwitchButton)
                .collect(ArrayList::new, (list, button) -> list.add(button), ArrayList::addAll);
    }

    private LeaderboardSwitchButton createSwitchButton(LeaderboardTypeConfig config) {
        boolean isCurrentType = config.type == leaderboardType;
        String state = isCurrentType ? "ENABLED" : "DISABLED";
        String configKey = "LEADERBOARD_TYPES_" + config.configKey;

        return new LeaderboardSwitchButton(
                MenusLocale.valueOf(configKey + "_SLOT").getInt(),
                config.type,
                MenusLocale.valueOf(configKey + "_" + state + "_TITLE").getString(),
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
                .collect(ArrayList::new, (list, line) -> list.add(line), ArrayList::addAll);
    }

    private String replacePlaceholders(String line, Kit kit, List<PlayerEntry> leaderboard) {
        String result = line;

        for (int i = 1; i <= MAX_LEADERBOARD_ENTRIES; i++) {
            PlayerEntry entry = getPlayerEntry(kit, leaderboard, i);

            String playerName = entry != null ? entry.getUsername() : PLACEHOLDER;
            String value = entry != null ? String.valueOf(entry.getValue()) : PLACEHOLDER;

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

    private record LeaderboardTypeConfig(LeaderboardType type, String configKey) {
    }
}