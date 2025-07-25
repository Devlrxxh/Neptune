package dev.lrxh.blockChanger.wrapper.impl.chunk.impl.v1_21;

import dev.lrxh.blockChanger.wrapper.impl.chunk.IChunkAccess;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class IChunkAccess_1_21 extends IChunkAccess {
    public IChunkAccess_1_21(Object input) {
        super(input);
    }

    @Override
    public Object[] getSections() {
        try {
            Class<?> chunkClass = nms().getClass();

            Class<?> chunkSectionClass = nms("world.level.chunk.ChunkSection");
            MethodHandle getSectionsHandle = getMethod(chunkClass, "d", Array.newInstance(chunkSectionClass, 0).getClass());
            return (Object[]) getSectionsHandle.invoke(nms());
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get chunk handle", e);
        }
    }

    @Override
    public void setSections(Object[] newSections) {
        Object[] currentSections = getSections();

        if (currentSections.length != newSections.length) {
            throw new IllegalArgumentException("Section count mismatch: expected "
                    + currentSections.length + ", but got " + newSections.length);
        }

        for (int i = 0; i < currentSections.length; i++) {
            currentSections[i] = copySection(newSections[i]);
        }
    }

    @Override
    public Object[] getSectionsCopy() {
        try {
            Object[] sections = getSections();

            List<Object> copiedSections = new ArrayList<>(sections.length);

            for (Object section : sections) {
                copiedSections.add(copySection(section));
            }

            return copiedSections.toArray(new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to get chunk handle", e);
        }
    }

    private Object copySection(Object section) {
        try {
            MethodHandle copyHandle = getMethod(section.getClass(), "k", section.getClass());
            return copyHandle.invoke(section);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to copy chunk section", e);
        }
    }
}
