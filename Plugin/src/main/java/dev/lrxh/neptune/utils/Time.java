package dev.lrxh.neptune.utils;

public class Time {
    private long oldTime;
    private long lastCurrentTime;
    private boolean stop;

    public Time() {
        this.oldTime = System.currentTimeMillis();
        this.stop = false;
    }

    public String formatTime() {
        if (!stop) {
            lastCurrentTime = System.currentTimeMillis();
        }

        long elapsedTime = lastCurrentTime - oldTime;

        long minutes = (elapsedTime / 1000) / 60;
        long seconds = (elapsedTime / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public void setZero() {
        this.oldTime = 0;
        this.lastCurrentTime = 0;
    }

    public void setStop(boolean stop) {
        this.stop = stop;

        if (!stop) {
            this.oldTime = System.currentTimeMillis();
        }
    }
}
