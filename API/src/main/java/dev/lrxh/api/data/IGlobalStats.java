package dev.lrxh.api.data;

public interface IGlobalStats {
    int getWins();
    void setWins(int amount);

    int getLosses();
    void setLosses(int amount);

    int getCurrentStreak();
    void setCurrentStreak(int amount);

    int getBestStreak();
    void setBestStreak(int amount);

    double getWinRatio();
}
