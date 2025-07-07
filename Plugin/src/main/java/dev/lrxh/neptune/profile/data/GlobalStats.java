package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalStats {
    private final Profile profile;
    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;
    private int elo = 0;
    private Division division;

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
            this.wins += kitData.getKills();
            this.losses += kitData.getDeaths();
            this.currentStreak += kitData.getCurrentStreak();
            this.bestStreak = Math.max(this.bestStreak, kitData.getBestStreak());
            this.elo += kitData.getElo();
        }
        int kitData = profile.getGameData().getKitData().size();
        if (kitData != 0) this.elo = this.elo / kitData;

        this.division = DivisionService.get().getDivisionByElo(elo);
    }

    public double getWinRatio() {
        int totalGames = wins + losses;
        if (totalGames == 0) return 0.0;
        return Math.round(((double) wins / totalGames) * 100);
    }
}
