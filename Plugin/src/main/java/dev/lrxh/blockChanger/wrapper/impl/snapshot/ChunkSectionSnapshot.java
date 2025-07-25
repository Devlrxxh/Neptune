package dev.lrxh.blockChanger.wrapper.impl.snapshot;

public class ChunkSectionSnapshot {
    private final Object[] sections;

    public ChunkSectionSnapshot(Object[] sections) {
        this.sections = sections;
    }

    public Object[] getSections() {
        return sections;
    }
}
