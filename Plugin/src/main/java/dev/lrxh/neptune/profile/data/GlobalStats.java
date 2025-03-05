package dev.lrxh.neptune.profile.data;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class GlobalStats {
    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;
    private int bestStreak = 0;

    public void addWins(int value) {
        this.wins += value;
    }

    public void addLosses(int value) {
        this.losses += value;
    }

    public void addCurrentStreak(int value) {
        this.currentStreak += value;

        if (currentStreak > bestStreak) {
            this.bestStreak = currentStreak;
        }
    }

    public double getWinRatio() {
        if (losses == 0) return wins;
        double kd = (double) wins / losses;
        BigDecimal bd = new BigDecimal(kd);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
