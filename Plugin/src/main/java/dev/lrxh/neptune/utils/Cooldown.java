package dev.lrxh.neptune.utils;

public class Cooldown {
    private long startTime;
    private final long durationMillis;

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

    public String formatRemaining() {
        long remaining = getRemainingTime();
        long seconds = (remaining / 1000);
        long millis = (remaining % 1000) / 10;
        return String.format("%02d.%02ds", seconds, millis);
    }

    public void reset() {
        start();
    }
}
