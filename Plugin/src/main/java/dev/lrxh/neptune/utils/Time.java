package dev.lrxh.neptune.utils;

public class Time {
    private int minutes;
    private int seconds;

    public Time() {
        this.minutes = 0;
        this.seconds = 0;
    }

    public void incrementTime() {
        seconds++;
        if (seconds == 60) {
            seconds = 0;
            minutes++;
        }
    }

    public String formatTime() {
        return String.format("%02d:%02d", minutes, seconds);
    }
}
