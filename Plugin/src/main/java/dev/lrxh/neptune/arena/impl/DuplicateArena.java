package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;
import org.bukkit.Bukkit;

public class DuplicateArena extends StandAloneArena {
    private final int offset;

    public DuplicateArena(StandAloneArena parent, int offset) {
        super(parent.getName() + "#" + offset, parent.getDisplayName(), LocationUtil.addOffsetX(parent.getRedSpawn(), offset),  LocationUtil.addOffsetX(parent.getBlueSpawn(), offset), LocationUtil.addOffsetX(parent.getMin(), offset), LocationUtil.addOffsetX(parent.getMax(), offset), parent.getLimit(), 0, true, false);
        this.offset = offset;
        restoreSnapshot();
    }

    @Override
    public void restoreSnapshot() {
        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> BlockChanger.paste(getSnapshot(), offset, 0));
    }
}
