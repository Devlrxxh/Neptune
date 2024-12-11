package dev.lrxh.neptune.profile.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GlobalStats {
    private int wins = 0;
    private int losses = 0;
    private int currentStreak = 0;

    public void addWins(int value) {
        this.wins += value;
    }

    public void addLosses(int value) {
        this.losses += value;
    }

    public void addCurrentStreak(int value) {
        this.currentStreak += value;
    }
}
