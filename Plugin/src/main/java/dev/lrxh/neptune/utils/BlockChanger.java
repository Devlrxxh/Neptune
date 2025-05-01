package dev.lrxh.neptune.utils;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Devlrxxh
 * @apiNote 1.8 - 1.21 easy to use util to be able to
 * set blocks blazingly fast
 */
@SuppressWarnings({"unused", "deprecation"})
public class BlockChanger {
    private static int MINOR_VERSION;
    private static JavaPlugin plugin;
    private static boolean debug;
    private static Long2ObjectMap<Object> worldCache;
    private static ExecutorService executorService;

    // NMS Classes
    private static Class<?> CRAFT_BLOCK_DATA;
    private static Class<?> LEVEL_HEIGHT_ACCESSOR;
    private static Class<?> CRAFT_WORLD;
    private static Class<?> WORLD_SERVER;
    private static Class<?> BLOCK;
    // NMS MethodHandles
    private static MethodHandle GET_STATE;
    private static MethodHandle GET_SECTIONS;
    private static MethodHandle GET_SECTION_INDEX;
    private static MethodHandle GET_CHUNK_AT;
    private static MethodHandle GET_HANDLE_WORLD;
    private static MethodHandle GET_COMBINED_ID;
    private static MethodHandle SET_TYPE;
    private static MethodHandle GET_BLOCK_DATA;
    // NMS Constructors
    private static Constructor<?> CHUNK_SECTION_CONSTRUCTOR;

    public static void load(JavaPlugin instance, boolean debug) {
        BlockChanger.plugin = instance;
        BlockChanger.MINOR_VERSION = getMinorVersion();
        BlockChanger.debug = debug;
        BlockChanger.worldCache = new Long2ObjectOpenHashMap<>();
        BlockChanger.executorService = Executors.newSingleThreadExecutor();

        init();
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param world  World to set block in.
     * @param blocks Set of locations and ItemStacks to be set
     */
    public static void setBlocks(World world, Set<BlockSnapshot> blocks) {
        long startTime = System.currentTimeMillis();
        HashMap<Long, Object> chunkCache = new HashMap<>();

        for (BlockSnapshot block : blocks) {
            setBlock(world, block, chunkCache);
        }

        Set<Long> chunkKeys = chunkCache.keySet();
        refreshChunks(world, chunkCache);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Pasted blocks time: " + duration + " ms (" + blocks.size() + ")");
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param world  World to set block in.
     * @param blocks Set of locations and ItemStacks to be set
     * @return A CompletableFuture that completes when the operation is done.
     */
    public static CompletableFuture<Void> setBlocksAsync(World world, Set<BlockSnapshot> blocks) {
        return CompletableFuture.runAsync(() -> setBlocks(world, blocks), executorService);
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param pos1     Position 1
     * @param pos2     Position 2
     * @param material Material to fill all blocks between pos1 and pos2
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static void setBlocks(Location pos1, Location pos2, Material material) {
        World world = pos1.getWorld();
        HashMap<Long, Object> chunkCache = new HashMap<>();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    setBlock(world, new BlockSnapshot(location, material), chunkCache);
                }
            }
        }

        refreshChunks(world, minX, minZ, maxX, maxZ);
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param pos1     Position 1
     * @param pos2     Position 2
     * @param material Material to fill all blocks between pos1 and pos2www
     * @return A CompletableFuture that completes when the operation is done.
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static CompletableFuture<Void> setBlocksAsync(Location pos1, Location pos2, Material material) {
        return CompletableFuture.runAsync(() -> setBlocks(pos1, pos2, material), executorService);
    }
    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param ignoreAir Position If air should be ignored
     * @return Snapshot captured snapshot
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static Set<BlockSnapshot> captureBlocks(Location pos1, Location pos2, boolean ignoreAir) {
        if (pos1 == null) {
            throw new IllegalArgumentException("pos1 must not be null");
        }

        if (pos2 == null) {
            throw new IllegalArgumentException("pos2 must not be null");
        }

        long startTime = System.currentTimeMillis();
        Location max = new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
        Location min = new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
        World world = max.getWorld();
        HashMap<Long, Object> chunkCache = new HashMap<>();

        Set<BlockSnapshot> snapshots = new HashSet<>();
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    BlockSnapshot blockData = new BlockSnapshot(location, getNMSBlockData(location.getChunk(), world, location, chunkCache));
                    if (ignoreAir && blockData.isAir()) continue;

                    snapshots.add(blockData);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Captured blocks time: " + duration + " ms");
        return snapshots;
    }
    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param ignoreAir Position If air should be ignored
     * @return A CompletableFuture containing the blocks captured.
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static CompletableFuture<Set<BlockSnapshot>> captureBlocksAsync(Location pos1, Location pos2, boolean ignoreAir) {
        return CompletableFuture.supplyAsync(() -> captureBlocks(pos1, pos2, ignoreAir), executorService);
    }

    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param ignoreAir Position If air should be ignored
     * @return Snapshot captured snapshot
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static Snapshot capture(Location pos1, Location pos2, boolean ignoreAir) {
        if (pos1 == null) {
            throw new IllegalArgumentException("pos1 must not be null");
        }

        if (pos2 == null) {
            throw new IllegalArgumentException("pos2 must not be null");
        }

        long startTime = System.currentTimeMillis();
        Location max = new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
        Location min = new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
        World world = max.getWorld();
        HashMap<Long, Object> chunkCache = new HashMap<>();

        Snapshot snapshot = new Snapshot(world, pos1, pos2);
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location location = new Location(world, x, y, z);
                    BlockSnapshot blockData = new BlockSnapshot(location, getNMSBlockData(location.getChunk(), world, location, chunkCache));
                    if (ignoreAir && blockData.isAir()) continue;

                    snapshot.add(blockData);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Snapshot capture time: " + duration + " ms");
        return snapshot;
    }

    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @param ignoreAir Position If air should be ignored
     * @return A CompletableFuture containing the Snapshot captured.
     * @see BlockChanger#loadChunks(Location, Location);
     */
    public static CompletableFuture<Snapshot> captureAsync(Location pos1, Location pos2, boolean ignoreAir) {
        return CompletableFuture.supplyAsync(() -> capture(pos1, pos2, ignoreAir), executorService);
    }

    /**
     * Revert all changes from the snapshot.
     *
     * @param snapshot Snapshot you have captured
     * @see BlockChanger#loadChunks(Snapshot);
     */
    public static void revert(Snapshot snapshot) {
        long startTime = System.currentTimeMillis();

        setBlocks(snapshot.world, snapshot.data);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Snapshot revert time: " + duration + " ms");
    }

    /**
     * Revert all changes from the snapshot.
     *
     * @param snapshot Snapshot you have captured
     * @return A CompletableFuture that completes when the operation is done.
     */
    public static CompletableFuture<Void> revertAsync(Snapshot snapshot) {
        return CompletableFuture.runAsync(() -> revert(snapshot), executorService);
    }

    /**
     * Load all chunks in a Snapshot
     *
     * @param snapshot Snapshot to load all chunks for.
     */
    public static void loadChunks(Snapshot snapshot) {
        loadChunks(snapshot.pos1, snapshot.pos2);
    }

    /**
     * Load all chunks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     */
    public static void loadChunks(Location pos1, Location pos2) {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int chunkStartX = minX >> 4;
        int chunkStartZ = minZ >> 4;
        int chunkEndX = maxX >> 4;
        int chunkEndZ = maxZ >> 4;
        World world = pos1.getWorld();

        for (int x = chunkStartX; x <= chunkEndX; x++) {
            for (int z = chunkStartZ; z <= chunkEndZ; z++) {
                world.loadChunk(x, z);
            }
        }
    }

    /**
     * Turn a Block into and ItemStack.
     *
     * @param block Block to be turned into an ItemStack
     * @return ItemStack
     */
    public static ItemStack fromBlock(Block block) {
        ItemStack itemStack = new ItemStack(block.getType());
        if (MINOR_VERSION == 8) {
            itemStack.setData(block.getState().getData());
        }

        return itemStack;
    }

    /**
     * Turn a BlockState into and ItemStack.
     *
     * @param blockState BlockState to be turned into an ItemStack
     * @return ItemStack
     */
    public static ItemStack fromBlockState(BlockState blockState) {
        ItemStack itemStack = new ItemStack(blockState.getType());
        if (MINOR_VERSION == 8) {
            itemStack.setData(blockState.getData());
        }

        return itemStack;
    }

    private static void refreshChunks(World world, HashMap<Long, Object> chunkCache) {
        for (long chunkKey : chunkCache.keySet()) {
            int chunkX = (int)(chunkKey >> 32);
            int chunkZ = (int)chunkKey;
            world.refreshChunk(chunkX, chunkZ);
        }
    }

    private static void setBlocks(World world, Object2ObjectOpenHashMap<Object, ObjectOpenHashSet<BlockLocation>> data) {
        long startTime = System.currentTimeMillis();
        HashMap<Long, Object> chunkCache = new HashMap<>();

        for (Map.Entry<Object, ObjectOpenHashSet<BlockLocation>> entry : data.entrySet()) {
            for (BlockLocation location : entry.getValue()) {
                setBlock(world, entry.getKey(), location, chunkCache);
            }
        }
        refreshChunks(world, chunkCache);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Pasted blocks time: " + duration + " ms");
    }

    private static void setBlock(World world, BlockSnapshot snapshot, HashMap<Long, Object> chunkCache) {
        setBlock(world, snapshot.blockDataNMS, snapshot.location, chunkCache);
    }

    private static void setBlock(World world, Object blockDataNMS, BlockLocation location, HashMap<Long, Object> chunkCache) {
        try {
            Object nmsChunk = getChunkAt(world, location, chunkCache);
            int x = location.x();
            int y = location.y();
            int z = location.z();

            Object cs = getSection(nmsChunk, y);

            SET_TYPE.invoke(cs, x & 15, y & 15, z & 15, blockDataNMS);
        } catch (Throwable e) {
            debug("Error occurred while at #setBlock(World, BlockSnapshot, HashMap) " + e.getMessage());
        }
    }

    private static void refreshChunks(World world, int minX, int minZ, int maxX, int maxZ) {
        int chunkStartX = minX >> 4;
        int chunkStartZ = minZ >> 4;
        int chunkEndX = maxX >> 4;
        int chunkEndZ = maxZ >> 4;

        for (int x = chunkStartX; x <= chunkEndX; x++) {
            for (int z = chunkStartZ; z <= chunkEndZ; z++) {
                world.refreshChunk(x, z);
            }
        }
    }

    private static Object getWorldNMS(World world) {
        long worldKey = world.getUID().getMostSignificantBits();
        Object cached = worldCache.get(worldKey);
        if (cached != null) return cached;

        try {
            Object craftWorld = CRAFT_WORLD.cast(world);
            Object worldServer = WORLD_SERVER.cast(GET_HANDLE_WORLD.invoke(craftWorld));
            worldCache.put(worldKey, worldServer);
            return worldServer;
        } catch (Throwable e) {
            debug("Error in getWorldNMS: " + e.getMessage());
        }
        return null;
    }

    private static Object getChunkNMS(Object world, Chunk chunk, HashMap<Long, Object> chunkCache) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

        Object cached = chunkCache.get(chunkKey);
        if (cached != null) return cached;

        try {
            Object nmsChunk = GET_CHUNK_AT.invoke(world, chunkX, chunkZ);
            chunkCache.put(chunkKey, nmsChunk);
            return nmsChunk;
        } catch (Throwable e) {
            debug("Error occurred while at #getChunkNMS(Object, Chunk, HashMap) " + e.getMessage());
        }
        return null;
    }

    private static Object getLevelHeightAccessor(Object nmsChunk) {
        try {
            return LEVEL_HEIGHT_ACCESSOR.cast(nmsChunk);
        } catch (Throwable e) {
            debug("Error occurred while at #getLevelHeightAccessor(Object) " + e.getMessage());
        }
        return null;
    }

    private static Object getBlockDataNMS(BlockData blockData) {
        try {
            return GET_STATE.invoke(CRAFT_BLOCK_DATA.cast(blockData));
        } catch (Throwable e) {
            debug("Error occurred while at #getBlockDataNMS(BlockData) " + e.getMessage());
        }
        return null;
    }

    private static Object getSection(Object nmsChunk, int index) {
        try {
            int sectionIndex = index >> 4;

            Object section;

            if (MINOR_VERSION != 8) {
                if (LEVEL_HEIGHT_ACCESSOR != null) {
                    Object LevelHeightAccessor = getLevelHeightAccessor(nmsChunk);

                    int i = (int) GET_SECTION_INDEX.invoke(LevelHeightAccessor, index);

                    section = getSections(nmsChunk)[i];
                } else {
                    section = getSections(nmsChunk)[sectionIndex];
                }
            } else {
                section = getSections(nmsChunk)[sectionIndex];
                if (section == null) {
                    section = CHUNK_SECTION_CONSTRUCTOR.newInstance(sectionIndex << 4, true);
                    getSections(nmsChunk)[sectionIndex] = section;
                }
            }

            return section;

        } catch (Throwable e) {
            return null;
        }
    }

    private static Object getNMSBlockData(Chunk chunk, World world, Location location, HashMap<Long, Object> chunkCache) {
        try {
            Object nmsChunk = getChunkAt(world, BlockLocation.fromLocation(location), chunkCache);
            int x = (int) location.getX();
            int y = location.getBlockY();
            int z = (int) location.getZ();

            Object cs = getSection(nmsChunk, y);
            if (cs == null) return null;

            return GET_BLOCK_DATA.invoke(cs, x & 15, y & 15, z & 15);
        } catch (Throwable e) {
            debug("Error occurred while at #getNMSBlockData(Chunk, World, Location, HashMap) " + e.getMessage());
        }

        return null;
    }

    private static Object getChunkAt(World world, BlockLocation location, HashMap<Long, Object> chunkCache) {
        int chunkX = location.x() >> 4;
        int chunkZ = location.z() >> 4;
        long chunkKey = ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);

        Object nmsWorld = getWorldNMS(world);
        Object nmsChunk = chunkCache.get(chunkKey);

        if (nmsChunk == null) {
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);
            nmsChunk = getChunkNMS(nmsWorld, chunk, chunkCache);
            chunkCache.put(chunkKey, nmsChunk);
        }

        return nmsChunk;
    }

    private static boolean isAir(Object blockData) {
        String name = blockData.toString().toLowerCase();
        if (name.contains("stair")) return false;
        if (name.contains("water")) return false;
        if (name.contains("wood")) return false;
        return name.contains("air");
    }

    private static Object[] getSections(Object nmsChunk) {
        try {
            return (Object[]) GET_SECTIONS.invoke(nmsChunk);
        } catch (Throwable e) {
            debug("Error occurred while at #getSections(Object) " + e.getMessage());
        }
        return new Object[0];
    }

    private static boolean supports(int version) {
        return MINOR_VERSION >= version;
    }

    private static int getMinorVersion() {
        String[] versionParts = plugin.getServer().getBukkitVersion().split("-")[0].split("\\.");
        if (versionParts.length >= 2) {
            return Integer.parseInt(versionParts[1]);
        }
        return 0;
    }

    private static void debug(String message) {
        if (debug) plugin.getLogger().info(message);
    }

    private static void init() {
        if (!supports(8)) {
            plugin.getLogger().info("Version Unsupported by BlockChanger");
            return;
        }

        String CRAFT_BUKKIT;
        String NET_MINECRAFT = "net.minecraft.";

        if (MINOR_VERSION == 16 || MINOR_VERSION == 8) {
            NET_MINECRAFT = "net.minecraft.server." + plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        }
        if (supports(21)) {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit.";
        } else {
            CRAFT_BUKKIT = "org.bukkit.craftbukkit." + plugin.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        }

        if (MINOR_VERSION == 8) {
            BLOCK = loadClass(NET_MINECRAFT + "Block");
        }

        Class<?> I_BLOCK_DATA;

        if (MINOR_VERSION != 16 && MINOR_VERSION != 8) {
            I_BLOCK_DATA = loadClass(NET_MINECRAFT + "world.level.block.state.IBlockData");
        } else {
            I_BLOCK_DATA = loadClass(NET_MINECRAFT + "IBlockData");
        }

        Class<?> CHUNK;
        if (MINOR_VERSION != 16 && MINOR_VERSION != 8) {
            CHUNK = loadClass(NET_MINECRAFT + "world.level.chunk.Chunk");
        } else {
            CHUNK = loadClass(NET_MINECRAFT + "Chunk");
        }

        Class<?> CHUNK_SECTION;

        if (MINOR_VERSION != 16 && MINOR_VERSION != 8) {
            CHUNK_SECTION = loadClass(NET_MINECRAFT + "world.level.chunk.ChunkSection");
        } else {
            CHUNK_SECTION = loadClass(NET_MINECRAFT + "ChunkSection");
        }

        if (MINOR_VERSION != 16 && MINOR_VERSION != 8) {
            WORLD_SERVER = loadClass(NET_MINECRAFT + "server.level.WorldServer");
        } else {
            WORLD_SERVER = loadClass(NET_MINECRAFT + "WorldServer");
        }

        Class<?> WORLD;
        if (MINOR_VERSION != 16 && MINOR_VERSION != 8) {
            WORLD = loadClass(NET_MINECRAFT + "world.level.World");
        } else {
            WORLD = loadClass(NET_MINECRAFT + "World");
        }

        if (MINOR_VERSION != 8) {
            if (MINOR_VERSION != 16) {
                LEVEL_HEIGHT_ACCESSOR = loadClass(NET_MINECRAFT + "world.level.LevelHeightAccessor");
            } else {
                LEVEL_HEIGHT_ACCESSOR = loadClass(NET_MINECRAFT + "LevelHeightAccessor");
            }
        }

        CRAFT_WORLD = loadClass(CRAFT_BUKKIT + "CraftWorld");

        Class<?> i_CHUNK_ACCESS = null;

        if (MINOR_VERSION != 8) {
            if (MINOR_VERSION != 16) {
                i_CHUNK_ACCESS = loadClass(NET_MINECRAFT + "world.level.chunk.IChunkAccess");
            } else {
                i_CHUNK_ACCESS = loadClass(NET_MINECRAFT + "IChunkAccess");
            }
        }

        if (MINOR_VERSION != 8) {
            CRAFT_BLOCK_DATA = loadClass(CRAFT_BUKKIT + "block.data.CraftBlockData");

            try {
                GET_STATE = getMethodHandle(CRAFT_BLOCK_DATA, "getState", I_BLOCK_DATA);
                debug("GET_STATE Loaded");
            } catch (Throwable e) {
                debug("GET_STATE didn't load " + e.getCause().getMessage());
            }
        }

        try {
            if (MINOR_VERSION != 8) {
                if (supports(21) || MINOR_VERSION == 16) {
                    GET_SECTIONS = getMethodHandle(i_CHUNK_ACCESS, "getSections", Array.newInstance(CHUNK_SECTION, 0).getClass());
                } else {
                    GET_SECTIONS = getMethodHandle(i_CHUNK_ACCESS, "d", Array.newInstance(CHUNK_SECTION, 0).getClass());
                }
            } else {
                GET_SECTIONS = getMethodHandle(CHUNK, "getSections", Array.newInstance(CHUNK_SECTION, 0).getClass());
            }

            debug("GET_SECTIONS Loaded");
        } catch (Throwable e) {
            debug("GET_SECTIONS didn't load " + e.getCause().getMessage());
        }

        if (MINOR_VERSION != 8) {
            try {
                if (MINOR_VERSION == 21) {
                    GET_SECTION_INDEX = getMethodHandle(LEVEL_HEIGHT_ACCESSOR, "f", int.class, int.class);
                } else if (supports(17)) {
                    GET_SECTION_INDEX = getMethodHandle(LEVEL_HEIGHT_ACCESSOR, "e", int.class, int.class);
                }
                debug("GET_SECTION_INDEX Loaded");
            } catch (Throwable e) {
                debug("GET_SECTIONS didn't load " + e.getCause().getMessage());
            }
        }

        try {
            if (MINOR_VERSION == 8) {
                GET_CHUNK_AT = getMethodHandle(WORLD, "getChunkAt", CHUNK, int.class, int.class);
                debug("GET_CHUNK_AT Loaded");
            } else {
                GET_CHUNK_AT = getMethodHandle(WORLD, "d", CHUNK, int.class, int.class);
                debug("GET_CHUNK_AT Loaded");
            }

        } catch (Throwable e) {
            debug("GET_CHUNK_AT didn't load " + e.getCause().getMessage());
        }

        try {
            GET_HANDLE_WORLD = getMethodHandle(CRAFT_WORLD, "getHandle", WORLD_SERVER);
            debug("GET_HANDLE_WORLD Loaded");
        } catch (Throwable e) {
            debug("GET_HANDLE_WORLD didn't load " + e.getCause().getMessage());
        }

        if (MINOR_VERSION == 8) {
            CHUNK_SECTION_CONSTRUCTOR = getConstructor(CHUNK_SECTION, int.class, boolean.class);
            debug("CHUNK_SECTION_CONSTRUCTOR Loaded");

            try {
                GET_COMBINED_ID = getMethodHandleStatic(BLOCK, "getByCombinedId", I_BLOCK_DATA, int.class);
                debug("GET_COMBINED_ID Loaded");
            } catch (Throwable e) {
                debug("GET_COMBINED_ID didn't load " + e.getCause().getMessage());
            }
        }

        if (MINOR_VERSION == 8) {
            try {
                SET_TYPE = getMethodHandle(CHUNK_SECTION, "setType", void.class, int.class, int.class, int.class, I_BLOCK_DATA);
                debug("SET_TYPE Loaded");
            } catch (Throwable e) {
                debug("SET_TYPE didn't load " + e.getCause().getMessage());
            }
        } else {
            try {
                SET_TYPE = getMethodHandle(CHUNK_SECTION, "a", I_BLOCK_DATA, int.class, int.class, int.class, I_BLOCK_DATA);
                debug("SET_TYPE Loaded");
            } catch (Throwable e) {
                debug("SET_TYPE didn't load " + e.getCause().getMessage());
            }
        }

        try {
            GET_BLOCK_DATA = getMethodHandle(CHUNK_SECTION, supports(16) ? "a" : "getType", I_BLOCK_DATA, int.class, int.class, int.class);
            debug("GET_BLOCK_DATA Loaded");
        } catch (Throwable e) {
            debug("GET_BLOCK_DATA didn't load " + e.getCause().getMessage());
        }
    }

    private static MethodHandle getMethodHandle(Class<?> clazz, String methodName, Class<?> type, Class<?>... parameterTypes) throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return lookup.findVirtual(clazz, methodName, MethodType.methodType(type, parameterTypes));
    }

    @SuppressWarnings("all")
    private static MethodHandle getMethodHandleStatic(Class<?> clazz, String methodName, Class<?> rtype, Class<?>... parameterTypes) throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return lookup.findStatic(clazz, methodName, MethodType.methodType(rtype, parameterTypes));
    }

    private static Class<?> loadClass(String className) {
        try {
            debug(className + " Loaded");
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            debug(className + " failed to load " + e.getMessage());
        }
        return null;
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (Exception e) {
            debug("Error occurred while at invokeConstructor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Snapshots contains all captured block data which can later be used to paste or revert.
     *
     * @see BlockChanger#capture(Location, Location, boolean)
     */
    public static class Snapshot {
        protected final World world;
        protected final Object2ObjectOpenHashMap<Object, ObjectOpenHashSet<BlockLocation>> data;
        protected final Location pos1, pos2;

        protected Snapshot(World world, Location pos1, Location pos2) {
            this.world = world;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.data = new Object2ObjectOpenHashMap<>();
        }

        protected void add(BlockSnapshot blockData) {
            Object nmsBlockData = blockData.blockDataNMS;
            BlockLocation location = blockData.location;

            if (data.get(nmsBlockData) == null) {
                ObjectOpenHashSet<BlockLocation> e = new ObjectOpenHashSet<>();
                e.add(location);
                data.put(nmsBlockData, e);
            } else {
                data.get(nmsBlockData).add(location);
            }
        }
    }

    /**
     * BlockChanger representation of blocks for multi version support
     */
    @SuppressWarnings("all")
    public static class BlockSnapshot {
        protected final Object blockDataNMS;
        protected BlockLocation location;

        /**
         * BlockChanger representation of blocks for multi version support
         * <p>
         * This is only works for 1.16 - 1.21 use the other constructors for 1.8
         *
         * @param blockData itemStack
         */
        public BlockSnapshot(Location location, BlockData blockData) {
            this.blockDataNMS = getBlockDataNMS(blockData);
            this.location = BlockLocation.fromLocation(location);
        }

        /**
         * BlockChanger representation of blocks for multi-version support
         * <p>
         * This is works for all versions.
         *
         * @param location The location of the block
         * @param material The material of the block
         */
        public BlockSnapshot(Location location, Material material) {
            if (MINOR_VERSION == 8) {
                ItemStack itemStack = new ItemStack(material);
                Object dataNMS = null;
                try {
                    dataNMS = GET_COMBINED_ID.invoke(itemStack.getType().getId() + (itemStack.getData().getData() << 12));
                } catch (Throwable throwable) {
                    debug("Error initializing blockDataNMS: " + throwable.getMessage());
                }
                this.blockDataNMS = dataNMS != null ? dataNMS : new Object();
            } else {
                this.blockDataNMS = getBlockDataNMS(material.createBlockData());
            }

            this.location = BlockLocation.fromLocation(location);
        }

        /**
         * BlockChanger representation of blocks for multi version support
         * <p>
         * This is works for all versions.
         *
         * @param itemStack itemStack
         */
        public BlockSnapshot(Location location, ItemStack itemStack) {
            if (MINOR_VERSION == 8) {
                Object dataNMS = null;
                try {
                    dataNMS = GET_COMBINED_ID.invoke(itemStack.getType().getId() + (itemStack.getData().getData() << 12));
                } catch (Throwable throwable) {
                    debug("Error initializing blockDataNMS: " + throwable.getMessage());
                }
                this.blockDataNMS = dataNMS != null ? dataNMS : new Object();
                this.location = BlockLocation.fromLocation(location);
            } else {
                this.blockDataNMS = getBlockDataNMS(itemStack.getType().createBlockData());
            }
        }

        protected BlockSnapshot(Location location, Object blockDataNMS) {
            this.location = BlockLocation.fromLocation(location);
            this.blockDataNMS = blockDataNMS;
        }

        protected BlockSnapshot(BlockLocation location, Object blockDataNMS) {
            this.location = location;
            this.blockDataNMS = blockDataNMS;
        }

        public boolean isAir() {
            return BlockChanger.isAir(blockDataNMS);
        }

        public BlockLocation getLocation() {
            return location;
        }

        public BlockSnapshot clone() {
            return new BlockSnapshot(location, blockDataNMS);
        }
    }

    public static class BlockLocation {
        private long packedCoords;

        protected BlockLocation(int x, int y, int z) {
            if (x < -33554432 || x > 33554431 ||
                    z < -33554432 || z > 33554431 ||
                    y < -2048 || y > 2047) {
                throw new IllegalArgumentException("Coordinates out of range for packing");
            }
            this.packedCoords = ((long)(x & 0x3FFFFFF) << 38) |
                    ((long)(z & 0x3FFFFFF) << 12) |
                    (y & 0xFFF);
        }

        protected static BlockLocation fromLocation(Location location) {
            return new BlockLocation(
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ()
            );
        }

        private void set(int x, int y, int z) {
            if (x < -33554432 || x > 33554431 ||
                    z < -33554432 || z > 33554431 ||
                    y < -2048 || y > 2047) {
                throw new IllegalArgumentException("Coordinates out of range for packing");
            }
            this.packedCoords = ((long)(x & 0x3FFFFFF) << 38) |
                    ((long)(z & 0x3FFFFFF) << 12) |
                    (y & 0xFFF);
        }

        public void add(int dx, int dy, int dz) {
            set(x() + dx, y() + dy, z() + dz);
        }

        protected Location toLocation(World world) {
            return new Location(
                    world,
                    (int)(packedCoords >> 38),  // X
                    (int)(packedCoords & 0xFFF), // Y
                    (int)(packedCoords << 26 >> 38) // Z
            );
        }

        protected int x() {
            return (int)(packedCoords >> 38);
        }

        protected int y() {
            return (int)(packedCoords << 52 >> 52);
        }

        protected int z() {
            return (int)((packedCoords >> 12) & 0x3FFFFFF) << 6 >> 6;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BlockLocation that = (BlockLocation) o;
            return packedCoords == that.packedCoords;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(packedCoords);
        }

        @Override
        public String toString() {
            return String.format("BlockLocation{x=%d, y=%d, z=%d}", x(), y(), z());
        }
    }
}