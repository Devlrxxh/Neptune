package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;

@UtilityClass
public class RandomUtils {
    private final SecureRandom rand = new SecureRandom();

    public float getRandFloat(float min, float max) {
        return min + rand.nextFloat() * (max - min);
    }

    public int getNextInt(int max) {
        return rand.nextInt(max);
    }
}
