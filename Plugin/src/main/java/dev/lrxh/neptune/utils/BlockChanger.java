package dev.lrxh.neptune.utils;

import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class BlockChanger {
    private final int MINOR_VERSION;
    private final JavaPlugin plugin;
    private final boolean debug;

    private final Set<Chunk> chunks;

    // NMS Classes
    private Class<?> CHUNK;
    private Class<?> CRAFT_CHUNK;
    private Class<?> CRAFT_BLOCK_DATA;

    // NMS METHODS
    private Method GET_STATE;
    private Method GET_HANDLE;
    private Method GET_SECTIONS;
    private Method SET_BLOCK_STATE;

    // NMS FIELDS
    private Field CHUNK_STATUS_FULL;

    public BlockChanger(JavaPlugin instance, boolean debug) {
        plugin = instance;
        MINOR_VERSION = extractMinorVersion();
        chunks = new HashSet<>();
        this.debug = debug;

        init();
    }

    @SneakyThrows
    private void init() {
        String CRAFT_BUKKIT;
        String NET_MINECRAFT = "net.minecraft.";

        if (MINOR_VERSION == 16) {
            NET_MINECRAFT = "net.minecraft.server." + plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        }
        if (supports(21)) {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit.";
        } else {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit." + plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        }

        Class<?> i_BLOCK_DATA;

        if (MINOR_VERSION != 16) {
            i_BLOCK_DATA = loadClass(NET_MINECRAFT + "world.level.block.state.IBlockData");
        } else {
            i_BLOCK_DATA = loadClass(NET_MINECRAFT + "IBlockData");
        }
        debug("I_BLOCK_DATA Loaded");

        if (MINOR_VERSION != 16) {
            CHUNK = loadClass(NET_MINECRAFT + "world.level.chunk.Chunk");
        } else {
            CHUNK = loadClass(NET_MINECRAFT + "Chunk");
        }
        debug("CHUNK Loaded");

        Class<?> CHUNK_SECTION;

        if (MINOR_VERSION != 16) {
            CHUNK_SECTION = loadClass(NET_MINECRAFT + "world.level.chunk.ChunkSection");
        } else {
            CHUNK_SECTION = loadClass(NET_MINECRAFT + "ChunkSection");
        }
        debug("CHUNK_SECTION Loaded");

        CRAFT_CHUNK = loadClass(CRAFT_BUKKIT + "CraftChunk");
        debug("CRAFT_CHUNK Loaded");

        Class<?> i_CHUNK_ACCESS;

        if (MINOR_VERSION != 16) {
            i_CHUNK_ACCESS = loadClass(NET_MINECRAFT + "world.level.chunk.IChunkAccess");
        } else {
            i_CHUNK_ACCESS = loadClass(NET_MINECRAFT + "IChunkAccess");
        }
        debug("I_CHUNK_ACCESS Loaded");

        CRAFT_BLOCK_DATA = loadClass(CRAFT_BUKKIT + "block.data.CraftBlockData");
        debug("CRAFT_BLOCK_DATA Loaded");

        Class<?> CHUNK_STATUS;
        if (MINOR_VERSION != 16) {
            if (supports(21)) {
                CHUNK_STATUS = loadClass(NET_MINECRAFT + "world.level.chunk.status.ChunkStatus");
            } else {
                CHUNK_STATUS = loadClass(NET_MINECRAFT + "world.level.chunk.ChunkStatus");
            }
        } else {
            CHUNK_STATUS = loadClass(NET_MINECRAFT + "ChunkStatus");
        }
        debug("CHUNK_STATUS Loaded");

        GET_STATE = getDeclaredMethod(CRAFT_BLOCK_DATA, "getState");
        debug("GET_STATE Loaded");

        if (MINOR_VERSION != 16) {
            GET_HANDLE = getDeclaredMethod(CRAFT_CHUNK, "getHandle", CHUNK_STATUS);
        } else {
            GET_HANDLE = getDeclaredMethod(CRAFT_CHUNK, "getHandle");
        }
        debug("GET_HANDLE Loaded");

        if (supports(21) || MINOR_VERSION == 16) {
            GET_SECTIONS = getDeclaredMethod(i_CHUNK_ACCESS, "getSections");
        } else {
            GET_SECTIONS = getDeclaredMethod(i_CHUNK_ACCESS, "d");
        }
        debug("GET_SECTIONS Loaded");

        if (MINOR_VERSION != 16) {
            if (supports(21)) {
                SET_BLOCK_STATE = getDeclaredMethod(CHUNK_SECTION, "setBlockState", int.class, int.class, int.class, i_BLOCK_DATA);
            } else {
                SET_BLOCK_STATE = getDeclaredMethod(CHUNK_SECTION, "a", int.class, int.class, int.class, i_BLOCK_DATA);
            }
        } else {
            SET_BLOCK_STATE = getDeclaredMethod(CHUNK_SECTION, "setType", int.class, int.class, int.class, i_BLOCK_DATA);
        }
        debug("SET_BLOCK_STATE Loaded");

        if (supports(21)) {
            CHUNK_STATUS_FULL = getDeclaredField(CHUNK_STATUS, "FULL");
        } else {
            CHUNK_STATUS_FULL = getDeclaredField(CHUNK_STATUS, "n");
        }
        debug("CHUNK_STATUS_FULL Loaded");
    }

    private void debug(String message) {
        if (debug) plugin.getLogger().info(message);
    }

    public void setBlock(Location location, BlockData blockData) {
        setBlockP(location, getBlockDataNMS(blockData));
    }

    @SneakyThrows
    private void setBlockP(Location location, Object iBlockData) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Object nmsChunk = getChunkNMS(location.getChunk()); // NET.MC.CHUNK

        Object cs = getSections(nmsChunk)[y >> 4]; // ORG.BUKKIT.CHUNKSECTION

        SET_BLOCK_STATE.invoke(cs, x & 15, y & 15, z & 15, iBlockData); // ORG.BUKKIT.CHUNKSECTION

        chunks.add(location.getChunk());
    }

    public Snapshot capture(Location min, Location max) {
        Snapshot snapshot = new Snapshot();
        World world = min.getWorld();
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = min.getWorld().getBlockAt(x, y, z);
                    Location location = new Location(world, x, y, z);
                    snapshot.add(location, block.getBlockData());
                }
            }
        }

        return snapshot;
    }

    public void revert(Snapshot snapshot) {
        for (Map.Entry<Location, BlockData> entry : snapshot.snapshot.entrySet()) {
            setBlock(entry.getKey(), entry.getValue());
        }

        notifyChanges();
    }

    public void notifyChanges() {
        for (Chunk chunk : chunks) {
            chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ());
        }

        chunks.clear();
    }

    @SneakyThrows
    private Object[] getSections(Object nmsChunk) {
        return (Object[]) GET_SECTIONS.invoke(nmsChunk);
    }

    @SneakyThrows
    private Object getBlockDataNMS(BlockData blockData) {
        return GET_STATE.invoke(CRAFT_BLOCK_DATA.cast(blockData));
    }

    @SneakyThrows
    private Object getChunkNMS(Chunk chunk) {
        if (MINOR_VERSION == 16) {
            Object craftChunk = CRAFT_CHUNK.cast(chunk);

            return GET_HANDLE.invoke(craftChunk);
        }

        Object craftChunk = CRAFT_CHUNK.cast(chunk);
        Object IChunkAccess = GET_HANDLE.invoke(craftChunk, CHUNK_STATUS_FULL.get(null));

        return CHUNK.cast(IChunkAccess);
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    private Field getDeclaredField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    private boolean supports(int version) {
        return MINOR_VERSION >= version;
    }

    private int extractMinorVersion() {
        String[] versionParts = plugin.getServer().getBukkitVersion().split("-")[0].split("\\.");
        if (versionParts.length >= 2) {

            return Integer.parseInt(versionParts[1]);
        }

        return 0;
    }

    public static class Snapshot {
        protected HashMap<Location, BlockData> snapshot;

        protected Snapshot() {
            snapshot = new HashMap<>();
        }

        protected void add(Location location, BlockData blockData) {
            snapshot.put(location, blockData.clone());
        }
    }
}
