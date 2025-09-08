package dev.lrxh.api.utils;

public interface ITime {
    String formatTime();
    String formatSecondsMillis();
    void setZero();
    void setStop(boolean stop);
}
