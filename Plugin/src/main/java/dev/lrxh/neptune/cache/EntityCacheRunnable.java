package dev.lrxh.neptune.cache;


import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

import java.util.Iterator;
import java.util.Map;
public class EntityCacheRunnable extends NeptuneRunnable {
    @Override
    public void run() {
        EntityCache.cleanEntityMap();
    }
}
