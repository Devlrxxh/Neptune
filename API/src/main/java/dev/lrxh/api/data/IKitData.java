package dev.lrxh.api.data;


public interface IKitData {

    int getKills();
    void setKills(int amount);

    int getDeaths();
    void setDeaths(int amount);

    int getCurrentStreak();
    void setCurrentStreak(int amount);

    int getBestStreak();
    void setBestStreak(int amount);

    double getKdr();

    void setCustomData(String key, Object value);
    Object getCustomData(String key);

    void setPersistentData(String key, Object value);
    Object getPersistentData(String key);
}
