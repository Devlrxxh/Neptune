package dev.lrxh.blockChanger.wrapper.impl.chunk.impl.v1_21;

import dev.lrxh.blockChanger.wrapper.impl.chunk.CraftChunk;
import dev.lrxh.blockChanger.wrapper.impl.chunk.IChunkAccess;
import org.bukkit.Chunk;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;

public class CraftChunk_1_21 extends CraftChunk {

    public CraftChunk_1_21(Chunk chunk) {
        super(chunk);
    }

    @Override
    protected Object apply(Chunk input) {
        return cb("CraftChunk").cast(input);
    }

    @Override
    public IChunkAccess getHandle() {
        try {
            Class<?> chunkStatusClass = nms("world.level.chunk.status.ChunkStatus");
            Class<?> iChunkAccessClass = nms("world.level.chunk.IChunkAccess");

            MethodHandle getHandle = getMethod(
                    nms().getClass(),
                    "getHandle",
                    iChunkAccessClass,
                    chunkStatusClass
            );

            Field field = getField(chunkStatusClass, "n"); // Field representing FULL
            if (field == null) {
                throw new RuntimeException("Field 'n' not found in ChunkStatus class");
            }

            Object fullStatus = getFieldValue(field);

            Object rawResult = getHandle.invoke(nms(), fullStatus);

            return IChunkAccess.from(rawResult);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get chunk handle", e);
        }
    }

}
