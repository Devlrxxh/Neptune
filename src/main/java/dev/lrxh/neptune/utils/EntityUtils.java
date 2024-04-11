package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityUtils {
    private int currentFakeEntityId = -1;

    public int getFakeEntityId() {
        return currentFakeEntityId--;
    }
}