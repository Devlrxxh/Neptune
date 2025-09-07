package dev.lrxh.neptune.feature.leaderboard.metadata;

import dev.lrxh.neptune.profile.data.KitData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
@AllArgsConstructor
public enum LeaderboardType {

    KILLS("Kills", "KILLS") {
        @Override
        public int get(KitData kitData) {
            return kitData.getKills();
        }
    },

    BEST_WIN_STREAK("Best Win Streak", "BEST_WIN_STREAK") {
        @Override
        public int get(KitData kitData) {
            return kitData.getBestStreak();
        }
    },

    ELO("Elo", "ELO") {
        @Override
        public int get(KitData kitData) {
            return kitData.getElo();
        }
    },

    DEATHS("Deaths", "DEATHS") {
        @Override
        public int get(KitData kitData) {
            return kitData.getDeaths();
        }
    };

    private final String name;
    private final String configName;

    /**
     * Retrieves the {@link LeaderboardType} corresponding to a string value (case-insensitive).
     *
     * @param value the string representation of the leaderboard type
     * @return the matching {@link LeaderboardType}, or {@code null} if none matches
     */
    @Nullable
    public static LeaderboardType value(String value) {
        for (LeaderboardType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Retrieves the numeric value of this leaderboard type from the given {@link KitData}.
     *
     * @param kitData the kit data
     * @return the value associated with this leaderboard type
     */
    public abstract int get(KitData kitData);
}
