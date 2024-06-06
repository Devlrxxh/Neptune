package dev.lrxh.neptune.leaderboard.impl;

import dev.lrxh.neptune.profile.data.KitData;

public enum LeaderboardType {
    WINS {
        @Override
        public int get(KitData kitData) {
            return kitData.getWins();
        }
    },
    DEATHS {
        @Override
        public int get(KitData kitData) {
            return kitData.getLosses();
        }
    };

    public abstract int get(KitData kitData);
}
