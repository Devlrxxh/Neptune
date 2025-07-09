package dev.lrxh.neptune.utils;

public class Cooldown {
    private final long durationMillis;
    private long startTime;

    public Cooldown(long durationMillis) {
        this.durationMillis = durationMillis;
        this.startTime = 0;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= durationMillis;
    }

    public long getRemainingTime() {
        long remaining = durationMillis - (System.currentTimeMillis() - startTime);
        return Math.max(remaining, 0);
    }

    public String formatSecondsMillis() {
        long remaining = getRemainingTime();
        long seconds = (remaining / 1000);
        long millis = (remaining % 1000) / 10;
        return String.format("%02d.%02ds", seconds, millis);
    }

    public String formatMinutesSeconds() {
        long remaining = getRemainingTime();
        long minutes = (remaining / 1000) / 60;
        long seconds = (remaining / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void reset() {
        start();
    }
}
