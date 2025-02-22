package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;

public class DuplicateArena extends StandAloneArena {
    private final int offset;
    private final StandAloneArena parent;

    public DuplicateArena(StandAloneArena parent, int offset) {
        super(parent.getName() + "#" + offset, parent.getDisplayName(), LocationUtil.addOffsetX(parent.getRedSpawn(), offset), LocationUtil.addOffsetX(parent.getBlueSpawn(), offset), LocationUtil.addOffsetX(parent.getMin(), offset), LocationUtil.addOffsetX(parent.getMax(), offset), parent.getLimit(), 0, true, true);
        this.offset = offset;
        this.parent = parent;

        restoreSnapshot();
    }

    @Override
    public void restoreSnapshot() {
        BlockChanger.pasteAsync(parent.getSnapshot(), offset, 0);
    }
}
