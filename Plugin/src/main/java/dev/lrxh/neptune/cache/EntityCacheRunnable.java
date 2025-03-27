package dev.lrxh.neptune.cache;

import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.entity.Entity;

import java.util.Iterator;
import java.util.Map;

public class EntityCacheRunnable extends NeptuneRunnable {
    @Override
    public void run() {
        Iterator<Map.Entry<Integer, Entity>> iterator = EntityCache.entityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Entity> entry = iterator.next();
            Entity entity = entry.getValue();
            if (entity == null || !entity.isValid() || entity.isDead()) {
                iterator.remove();
            }
        }

    }
}
