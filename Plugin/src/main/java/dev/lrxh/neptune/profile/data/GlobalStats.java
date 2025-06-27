package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.profile.impl.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalStats {
    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;
    private final Profile profile;
    private int elo = 0;

    public GlobalStats(Profile profile) {
        this.profile = profile;
    }

    public void update() {
        this.wins = 0;
        this.losses = 0;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.elo = 0;

        for (KitData kitData : profile.getGameData().getKitData().values()) {
            this.wins += kitData.getWins();
            this.losses += kitData.getLosses();
            this.currentStreak += kitData.getCurrentStreak();
            this.bestStreak = Math.max(this.bestStreak, kitData.getBestStreak());
            this.elo += kitData.getElo();
        }
    }

    public double getWinRatio() {
        int totalGames = wins + losses;
        return Math.round(((double) wins / totalGames) * 100);
    }
}
