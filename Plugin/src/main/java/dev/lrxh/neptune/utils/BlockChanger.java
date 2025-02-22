package dev.lrxh.neptune.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Devlrxxh
 * @apiNote 1.8 - 1.21.4 easy to use util to be able to
 * set blocks blazingly fast
 */
public class BlockChanger {
    private static int MINOR_VERSION;
    private static JavaPlugin plugin;
    private static boolean debug;
    private static HashMap<String, Object> worldCache;

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
        BlockChanger.worldCache = new HashMap<>();

        init();
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param world  World to set block in.
     * @param blocks Map of locations and ItemStacks to be set
     */
    public static void setBlocks(World world, List<BlockSnapshot> blocks) {
        long startTime = System.currentTimeMillis();
        HashMap<Chunk, Object> chunkCache = new HashMap<>();

        for (BlockSnapshot block : blocks) {
            setBlock(block, chunkCache);
        }

        for (Chunk chunk : chunkCache.keySet()) {
            world.refreshChunk(chunk.getX(), chunk.getZ());
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Pasted blocks time: " + duration + " ms (" + blocks.size() + ")");
    }

    /**
     * Sets blocks block-data's using NMS.
     *
     * @param world  World to set block in.
     * @param blocks Map of locations and ItemStacks to be set
     * @return A CompletableFuture that completes when the paste operation is done.
     */
    public static CompletableFuture<Void> setBlocksAsync(World world, List<BlockSnapshot> blocks) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();
            HashMap<Chunk, Object> chunkCache = new HashMap<>();

            for (BlockSnapshot block : blocks) {
                setBlock(block, chunkCache);
            }

            for (Chunk chunk : chunkCache.keySet()) {
                world.refreshChunk(chunk.getX(), chunk.getZ());
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            debug("Pasted blocks time: " + duration + " ms (" + blocks.size() + ")");
        });
    }

    /**
     * Paste a snapshot and allowing an offset
     *
     * @param snapshot Captured Snapshot.
     * @param offsetX  The offset to apply to the X coordinate of each block.
     * @param offsetZ  The offset to apply to the Z coordinate of each block.
     */
    public static void paste(Snapshot snapshot, int offsetX, int offsetZ) {
        List<BlockSnapshot> blocks = new ArrayList<>();

        for (BlockSnapshot blockSnapshot : snapshot.blocks) {
            BlockSnapshot b1 = blockSnapshot.clone();
            b1.addOffset(offsetX, offsetZ);
            blocks.add(b1);
        }

        setBlocks(snapshot.world, blocks);
    }

    /**
     * Paste a snapshot and allowing an offset
     *
     * @param snapshot Captured Snapshot.
     * @param offsetX  The offset to apply to the X coordinate of each block.
     * @param offsetZ  The offset to apply to the Z coordinate of each block.
     * @return A CompletableFuture that completes when the paste operation is done.
     */
    public static CompletableFuture<Void> pasteAsync(Snapshot snapshot, int offsetX, int offsetZ) {
        return CompletableFuture.runAsync(() -> {
            List<BlockSnapshot> blocks = new ArrayList<>();

            for (BlockSnapshot blockSnapshot : snapshot.blocks) {
                BlockSnapshot b1 = blockSnapshot.clone();
                b1.addOffset(offsetX, offsetZ);
                blocks.add(b1);
            }

            setBlocks(snapshot.world, blocks);
        });
    }


    /**
     * Paste a snapshot and allowing an offset
     *
     * @param snapshot Captured Snapshot.
     * @param pos New location to paste snapshot at.
     */
    public static void paste(Snapshot snapshot, Location pos) {
        List<BlockSnapshot> blocks = new ArrayList<>();
        int offsetX = (int) (snapshot.pos.getX() - pos.getX());
        int offsetZ = (int) (snapshot.pos.getZ() - pos.getZ());

        debug(pos.toString());

        for (BlockSnapshot blockSnapshot : snapshot.blocks) {
            BlockSnapshot b1 = blockSnapshot.clone();

            b1.addOffset(offsetX, offsetZ);
            blocks.add(b1);
        }

        setBlocks(pos.getWorld(), blocks);
    }

    /**
     * Paste a snapshot and allowing an offset
     *
     * @param snapshot Captured Snapshot.
     * @param pos      New location to paste snapshot at.
     * @return A CompletableFuture that completes when the paste operation is done.
     */
    public static CompletableFuture<Void> pasteAsync(Snapshot snapshot, Location pos) {
        return CompletableFuture.runAsync(() -> {
            List<BlockSnapshot> blocks = new ArrayList<>();
            int offsetX = (int) (snapshot.pos.getX() - pos.getX());
            int offsetZ = (int) (snapshot.pos.getZ() - pos.getZ());

            debug(pos.toString());

            for (BlockSnapshot blockSnapshot : snapshot.blocks) {
                BlockSnapshot b1 = blockSnapshot.clone();

                b1.addOffset(offsetX, offsetZ);
                blocks.add(b1);
            }

            setBlocks(pos.getWorld(), blocks);
        });
    }

    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return Snapshot captured snapshot
     */
    public static Snapshot capture(Location pos1, Location pos2) {
        Location max = new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
        Location min = new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
        World world = max.getWorld();
        HashMap<Chunk, Object> chunkCache = new HashMap<>();

        Snapshot snapshot = new Snapshot(world, pos1);
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
                    snapshot.add(new BlockSnapshot(location, getNMSBlockData(location.getChunk(), world, location, chunkCache)));
                }
            }
        }
        debug("Captured Snapshot (" + snapshot.blocks.size() + ")");
        return snapshot;
    }


    /**
     * Capture all blocks between 2 positions
     *
     * @param pos1 Position 1
     * @param pos2 Position 2
     * @return A CompletableFuture containing the Snapshot object.
     * The snapshot can be used to revert the captured blocks at a later time.
     */
    public static CompletableFuture<Snapshot> captureAsync(Location pos1, Location pos2) {
        return CompletableFuture.supplyAsync(() -> {
            Location max = new Location(pos1.getWorld(), Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
            Location min = new Location(pos1.getWorld(), Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
            World world = max.getWorld();
            HashMap<Chunk, Object> chunkCache = new HashMap<>();

            Snapshot snapshot = new Snapshot(world, pos1);
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
                        snapshot.add(new BlockSnapshot(location, getNMSBlockData(location.getChunk(), world, location, chunkCache)));
                    }
                }
            }


            debug("Captured Snapshot (" + snapshot.blocks.size() + ")");
            return snapshot;
        });
    }

    /**
     * Revert all changes from the snapshot.
     *
     * @param snapshot Snapshot you have captured
     */
    public static void revert(World world, Snapshot snapshot) {
        long startTime = System.currentTimeMillis();

        setBlocks(world, snapshot.blocks);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        debug("Snapshot revert time: " + duration + " ms (" + snapshot.blocks.size() + ")");
    }

    /**
     * Revert all changes from the snapshot.
     *
     * @param snapshot Snapshot you have captured
     * @return A CompletableFuture that completes when the paste operation is done.
     */
    public static CompletableFuture<Void> revertAsync(World world, Snapshot snapshot) {
        return CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();

            setBlocks(world, snapshot.blocks);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            debug("Snapshot revert time: " + duration + " ms (" + snapshot.blocks.size() + ")");
        });
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

    private static void setBlock(BlockSnapshot snapshot, HashMap<Chunk, Object> chunkCache) {
        try {
            Object nmsBlockData = snapshot.blockDataNMS;
            Location location = snapshot.location;

            Chunk chunk = location.getChunk();
            Object nmsWorld = getWorldNMS(snapshot.location.getWorld());
            Object nmsChunk = getChunkNMS(nmsWorld, chunk, chunkCache);

            int x = (int) location.getX();
            int y = location.getBlockY();
            int z = (int) location.getZ();

            Object cs = getSection(nmsChunk, y);
            if (cs == null) return;

            SET_TYPE.invoke(cs, x & 15, y & 15, z & 15, nmsBlockData);

        } catch (Throwable e) {
            debug("Error occurred while at #setBlock(BlockSnapshot, HashMap) " + e.getMessage());
        }
    }

    private static Object getWorldNMS(World world) {
        Object c = worldCache.get(world.getName());
        if (c != null) {
            return c;
        } else {
            worldCache.remove(world.getName());
        }

        try {
            Object craftWorld = CRAFT_WORLD.cast(world);
            Object worldServer = WORLD_SERVER.cast(GET_HANDLE_WORLD.invoke(craftWorld));
            worldCache.put(world.getName(), worldServer);

            return worldServer;
        } catch (Throwable e) {
            debug("Error occurred while at #getWorldNMS(World) " + e.getMessage());
        }
        return null;
    }

    private static Object getChunkNMS(Object world, Chunk chunk, HashMap<Chunk, Object> chunkCache) {
        Object c = chunkCache.get(chunk);
        if (c != null) return c;

        try {
            Object nmsChunk = GET_CHUNK_AT.invoke(world, chunk.getX(), chunk.getZ());

            chunkCache.put(chunk, nmsChunk);

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
            if (MINOR_VERSION != 8) {
                if (LEVEL_HEIGHT_ACCESSOR != null) {
                    Object LevelHeightAccessor = getLevelHeightAccessor(nmsChunk);

                    int i = (int) GET_SECTION_INDEX.invoke(LevelHeightAccessor, index);

                    return getSections(nmsChunk)[i];
                } else {
                    return getSections(nmsChunk)[index >> 4];
                }
            } else {
                int sectionIndex = index >> 4;
                Object cs = getSections(nmsChunk)[sectionIndex];
                if (cs == null) {
                    cs = CHUNK_SECTION_CONSTRUCTOR.newInstance(sectionIndex << 4, true);
                    getSections(nmsChunk)[sectionIndex] = cs;
                }

                return cs;
            }

        } catch (Throwable e) {
            return null;
        }
    }

    private static Object getNMSBlockData(Chunk chunk, World world, Location location, HashMap<Chunk, Object> chunkCache) {
        try {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!chunk.isLoaded()) chunk.load();
            });
            Object nmsWorld = getWorldNMS(world);
            Object nmsChunk = getChunkNMS(nmsWorld, chunk, chunkCache);

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

        CRAFT_BLOCK_DATA = loadClass(CRAFT_BUKKIT + "block.data.CraftBlockData");

        if (MINOR_VERSION != 8) {
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
            GET_BLOCK_DATA = getMethodHandle(CHUNK_SECTION, "a", I_BLOCK_DATA, int.class, int.class, int.class);
            debug("GET_BLOCK_DATA Loaded");
        } catch (Throwable e) {
            debug("GET_BLOCK_DATA didn't load " + e.getCause().getMessage());
        }
    }

    private static MethodHandle getMethodHandle(Class<?> clazz, String methodName, Class<?> rtype, Class<?>... parameterTypes) throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return lookup.findVirtual(clazz, methodName, MethodType.methodType(rtype, parameterTypes));
    }

    private static MethodHandle getMethodHandleStatic(Class<?> clazz, String methodName, Class<?> rtype, Class<?>... parameterTypes) throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        return lookup.findStatic(clazz, methodName, MethodType.methodType(rtype, parameterTypes));
    }


    private static Object[] getSections(Object nmsChunk) {
        try {
            return (Object[]) GET_SECTIONS.invoke(nmsChunk);
        } catch (Throwable e) {
            debug("Error occurred while at #getSections(Object) " + e.getMessage());
        }
        return new Object[0];
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

    private static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            debug("Error occurred while at #getDeclaredField(Class<?>, String) " + e.getMessage());
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

    private static void printAllMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            System.out.print("Method: " + method.getName());
            System.out.print(" | Return type: " + method.getReturnType().getSimpleName());
            System.out.print(" | Modifiers: " + Modifier.toString(method.getModifiers()));
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

    private static void printAllFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.print("Field: " + field.getName());
            System.out.print(" | Type: " + field.getType().getSimpleName());
            System.out.print(" | Modifiers: " + Modifier.toString(field.getModifiers()));
            System.out.println();
        }
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

    public static class Snapshot {
        protected final World world;
        protected final List<BlockSnapshot> blocks;
        protected final Location pos;

        protected Snapshot(World world, Location pos) {
            this.world = world;
            this.pos = pos;
            this.blocks = new ArrayList<>();
        }

        protected void add(BlockSnapshot blockData) {
            blocks.add(blockData);
        }
    }

    public static class BlockSnapshot {
        protected final Object blockDataNMS;
        protected Location location;
        /**
         * BlockChanger representation of blocks for multi version support
         * <p>
         * This is only works for 1.16 - 1.21 use the other constructors for 1.8
         *
         * @param blockData itemStack
         */
        public BlockSnapshot(Location location, BlockData blockData) {
            this.blockDataNMS = getBlockDataNMS(blockData);
            this.location = location;
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
                    System.err.println("Error initializing blockDataNMS: " + throwable.getMessage());
                }
                this.blockDataNMS = dataNMS != null ? dataNMS : new Object();
            } else {
                this.blockDataNMS = getBlockDataNMS(material.createBlockData());
            }

            this.location = location;
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
                    System.err.println("Error initializing blockDataNMS: " + throwable.getMessage());
                }
                this.blockDataNMS = dataNMS != null ? dataNMS : new Object();
                this.location = location;
            } else {
                this.blockDataNMS = getBlockDataNMS(itemStack.getType().createBlockData());
            }
        }

        protected BlockSnapshot(Location location, Object blockDataNMS) {
            this.location = location;
            this.blockDataNMS = blockDataNMS;
        }

        protected BlockSnapshot clone() {
            return new BlockSnapshot(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()), blockDataNMS);
        }

        protected void addOffset(int offsetX, int offsetZ) {
            this.location.add(offsetX, 0, offsetZ);
        }
    }
}