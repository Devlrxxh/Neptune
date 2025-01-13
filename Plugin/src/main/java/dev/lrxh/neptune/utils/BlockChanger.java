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
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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

        if (supports(21)) {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit.";
        } else {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit." + plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        }

        Class<?> i_BLOCK_DATA = loadClass(NET_MINECRAFT + "world.level.block.state.IBlockData");
        debug("I_BLOCK_DATA Loaded");

        CHUNK = loadClass(NET_MINECRAFT + "world.level.chunk.Chunk");
        debug("CHUNK Loaded");

        Class<?> CHUNK_SECTION = loadClass(NET_MINECRAFT + "world.level.chunk.ChunkSection");
        debug("CHUNK_SECTION Loaded");

        CRAFT_CHUNK = loadClass(CRAFT_BUKKIT + "CraftChunk");
        debug("CRAFT_CHUNK Loaded");

        Class<?> i_CHUNK_ACCESS = loadClass(NET_MINECRAFT + "world.level.chunk.IChunkAccess");
        debug("I_CHUNK_ACCESS Loaded");

        CRAFT_BLOCK_DATA = loadClass(CRAFT_BUKKIT + "block.data.CraftBlockData");
        debug("CRAFT_BLOCK_DATA Loaded");

        Class<?> CHUNK_STATUS;
        if (supports(21)) {
            CHUNK_STATUS = loadClass(NET_MINECRAFT + "world.level.chunk.status.ChunkStatus");
        } else {
            CHUNK_STATUS = loadClass(NET_MINECRAFT + "world.level.chunk.ChunkStatus");
        }
        debug("CHUNK_STATUS Loaded");

        GET_STATE = getDeclaredMethod(CRAFT_BLOCK_DATA, "getState");
        debug("GET_STATE Loaded");

        GET_HANDLE = getDeclaredMethod(CRAFT_CHUNK, "getHandle", CHUNK_STATUS);
        debug("GET_HANDLE Loaded");

        if (supports(21)) {
            GET_SECTIONS = getDeclaredMethod(i_CHUNK_ACCESS, "getSections");
        } else {
            GET_SECTIONS = getDeclaredMethod(i_CHUNK_ACCESS, "d");
        }

        debug("GET_SECTIONS Loaded");

        if (supports(21)) {
            SET_BLOCK_STATE = getDeclaredMethod(CHUNK_SECTION, "setBlockState", int.class, int.class, int.class, i_BLOCK_DATA);
        } else {
            SET_BLOCK_STATE = getDeclaredMethod(CHUNK_SECTION, "a", int.class, int.class, int.class, i_BLOCK_DATA);
        }
        debug("SET_BLOCK_STATE Loaded");

        if (supports(21)) {
            CHUNK_STATUS_FULL = getDeclaredField(CHUNK_STATUS, "FULL");
        } else {
            CHUNK_STATUS_FULL = getDeclaredField(CHUNK_STATUS, "n");
        }
        debug("CHUNK_STATUS_FULL Loaded");
    }

    private void printAllMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            System.out.print("Method: " + method.getName());
            System.out.print(" | Return type: " + method.getReturnType().getSimpleName());
            System.out.print(" | Modifiers: " + java.lang.reflect.Modifier.toString(method.getModifiers()));
            System.out.print(" | Parameters: ");
            Parameter[] parameters = method.getParameters();
            if (parameters.length == 0) {
                System.out.print("None");
            } else {
                for (Parameter param : parameters) {
                    System.out.print(param.getType().getSimpleName() + " " + param.getName() + ", ");
                }
                System.out.print("\b\b");
            }

            System.out.println();
        }
    }

    private void printAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            System.out.print("Field: " + field.getName());
            System.out.print(" | Type: " + field.getType().getSimpleName());
            System.out.print(" | Modifiers: " + Modifier.toString(field.getModifiers()));
            System.out.println();
        }
    }

    private void debug(String message) {
        if (debug) plugin.getLogger().info(message);
    }

    public void setBlock(Location location, BlockData blockData) {
        setBlock(location, getBlockDataNMS(blockData));
    }

    @SneakyThrows
    private void setBlock(Location location, Object iBlockData) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Object nmsChunk = getChunkNMS(location.getChunk()); // NET.MC.CHUNK

        Object cs = getSections(nmsChunk)[y >> 4]; // ORG.BUKKIT.CHUNKSECTION

        SET_BLOCK_STATE.invoke(cs, x & 15, y & 15, z & 15, iBlockData); // ORG.BUKKIT.CHUNKSECTION

        chunks.add(location.getChunk());
    }

    public Snapshot capture(Location min, Location max, int offset) {
        Location maxx = LocationUtil.addOffsetToLocation(max, offset);
        Location minn = LocationUtil.addOffsetToLocation(min, offset);

        Snapshot snapshot = new Snapshot();
        World world = max.getWorld();
        int minX = Math.min(minn.getBlockX(), maxx.getBlockX());
        int minY = Math.min(minn.getBlockY(), maxx.getBlockY());
        int minZ = Math.min(minn.getBlockZ(), maxx.getBlockZ());

        int maxX = Math.max(minn.getBlockX(), maxx.getBlockX());
        int maxY = Math.max(minn.getBlockY(), maxx.getBlockY());
        int maxZ = Math.max(minn.getBlockZ(), maxx.getBlockZ());

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

        public Snapshot() {
            snapshot = new HashMap<>();
        }

        protected void add(Location location, BlockData blockData) {
            snapshot.put(location, blockData);
        }
    }
}