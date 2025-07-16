package dev.lrxh.neptune.cache;


import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

public class EntityCacheRunnable extends NeptuneRunnable {
    @Override
    public void run() {
        EntityCache.cleanEntityMap();
    }
}
