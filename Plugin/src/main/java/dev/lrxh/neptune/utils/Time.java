package dev.lrxh.neptune.utils;

public class Time {
    private final long oldTime;

    public Time() {
        this.oldTime = System.currentTimeMillis();
    }

    public String formatTime() {
        long currentTime = System.currentTimeMillis();

        long elapsedTime = currentTime - oldTime;

        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }
}
