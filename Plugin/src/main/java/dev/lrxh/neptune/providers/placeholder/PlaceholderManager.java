package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.providers.placeholder.impl.*;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceholderManager {
    private static PlaceholderManager instance;
    private final List<Placeholder> placeholders;

    public PlaceholderManager() {
        this.placeholders = new ArrayList<>();

        placeholders.addAll(Arrays.asList(
                new PingPlaceholder(),
                new InMatchPlaceholder(),
                new QueuedPlaceholder(),
                new WinsPlaceholder(),
                new LossesPlaceholder(),
                new CurrentStreakPlaceholder(),
                new BestStreakPlaceholder(),
                new LastKitPlaceholder(),
                new ColorPlaceholder(),
                new KitDivisionPlaceholder(),
                new KitWinsPlaceholder(),
                new KitLossesPlaceholder(),
                new KitCurrentStreakPlaceholder(),
                new KitBestStreakPlaceholder(),
                new LeaderboardPlaceholder(),
                new KitInMatchPlaceholder(),
                new KitQueuedPlaceholder(),
                new WinRatePlaceholder(),
                new OpponentPlaceholder(),
                new OpponentPingPlaceholder(),
                new ComboPlaceholder(),
                new OpponentComboPlaceholder(),
                new HitsPlaceholder(),
                new OpponentHitsPlaceholder(),
                new HitDifferencePlaceholder(),
                new TimePlaceholder(),
                new KitPlaceholder(),
                new ArenaPlaceholder(),
                new MaxPointsPlaceholder(),
                new PointsPlaceholder(),
                new OpponentPointsPlaceholder(),
                new BedBrokenPlaceholder(),
                new OpponentBedBrokenPlaceholder(),
                new PlayerRedNamePlaceholder(),
                new PlayerBlueNamePlaceholder(),
                new PlayerRedPingPlaceholder(),
                new PlayerBluePingPlaceholder(),
                new RedBedBrokenPlaceholder(),
                new BlueBedBrokenPlaceholder(),
                new AlivePlaceholder(),
                new OpponentAlivePlaceholder(),
                new MaxPlaceholder(),
                new OpponentMaxPlaceholder(),
                new InQueuePlaceholder(),
                new QueueTimePlaceholder(),
                new IsTeamMatchPlaceholder(),
                new KitEloPlaceholder(),
                new DivisionPlaceholder(),
                new EloPlaceholder()
        ));
    }

    public static PlaceholderManager get() {
        if (instance == null) instance = new PlaceholderManager();

        return instance;
    }

    public String parse(OfflinePlayer player, String text) {
        for (Placeholder placeholder : placeholders) {
            text = placeholder.parse(player, text);
        }

        return text;
    }
}
