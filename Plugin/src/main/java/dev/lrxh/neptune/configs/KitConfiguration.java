package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.BlockWhitelistConfig;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A unified configuration system for kits that combines KitRule and blockwhitelist.yml
 * This class provides a single API for accessing all kit configuration options.
 */
@Getter
public class KitConfiguration {
    private static KitConfiguration instance;
    private final BlockWhitelistConfig blockWhitelistConfig;

    // Cache kit configurations by kit name
    private final Map<String, KitConfigData> kitConfigs = new HashMap<>();

    /**
     * Create a new KitConfiguration instance
     * This will initialize the BlockWhitelistConfig and load all kit configurations
     */
    public KitConfiguration() {
        this.blockWhitelistConfig = BlockWhitelistConfig.get();
        instance = this;
    }

    /**
     * Get the singleton instance of KitConfiguration
     *
     * @return The KitConfiguration instance
     */
    public static KitConfiguration get() {
        if (instance == null) {
            instance = new KitConfiguration();
        }
        return instance;
    }

    /**
     * Get configuration data for a specific kit
     *
     * @param kitName The name of the kit
     * @return The KitConfigData for the specified kit
     */
    public KitConfigData getKitConfig(String kitName) {
        return kitConfigs.computeIfAbsent(kitName.toLowerCase(), this::loadKitConfig);
    }

    /**
     * Get configuration data for a specific kit
     *
     * @param kit The kit
     * @return The KitConfigData for the specified kit
     */
    public KitConfigData getKitConfig(Kit kit) {
        return getKitConfig(kit.getName());
    }

    /**
     * Load configuration data for a specific kit
     *
     * @param kitName The name of the kit
     * @return The KitConfigData for the specified kit
     */
    private KitConfigData loadKitConfig(String kitName) {
        // Ensure the kit has a default whitelist entry
        blockWhitelistConfig.createDefaultWhitelistForKit(kitName);

        // Get the whitelisted blocks for this kit
        Set<Material> whitelistedBlocks = blockWhitelistConfig.getWhitelistedBlocks(kitName);
        String errorMessage = blockWhitelistConfig.getErrorMessage(kitName);

        return new KitConfigData(kitName, whitelistedBlocks, errorMessage);
    }

    /**
     * Reload all kit configurations
     * This will clear the cache and reload from disk
     */
    public void reload() {
        // Reload the block whitelist config
        blockWhitelistConfig.loadConfig();

        // Clear the cache
        kitConfigs.clear();
    }

    /**
     * Save a kit's configuration to disk
     *
     * @param kit The kit to save
     */
    public void saveKit(Kit kit) {
        // Update the block whitelist with the kit's configuration
        KitConfigData configData = getKitConfig(kit);

        // If we have cached config data, update it
        if (configData != null) {
            // Save any changes back to the blockwhitelist.yml
            File file = new File(Neptune.get().getDataFolder(), "blockwhitelist.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            // Only update if the kit has a section in the config
            if (config.contains(kit.getName())) {
                // Update blocks if the kit has custom blocks
                if (!configData.getWhitelistedBlocks().isEmpty()) {
                    List<String> blockList = configData.getWhitelistedBlocks().stream()
                            .map(Material::name)
                            .collect(Collectors.toList());
                    config.set(kit.getName() + ".blocks", blockList);
                }

                // Update error message if the kit has a custom message
                if (configData.getErrorMessage() != null) {
                    config.set(kit.getName() + ".message", configData.getErrorMessage());
                }

                try {
                    config.save(file);
                } catch (Exception e) {
                    Neptune.get().getLogger().log(Level.WARNING,
                            "Failed to save block whitelist for kit: " + kit.getName(), e);
                }
            }
        }
    }

    /**
     * Data class to hold kit configuration data
     */
    @Getter
    public static class KitConfigData {
        private final String kitName;
        private final Set<Material> whitelistedBlocks;
        private String errorMessage;

        public KitConfigData(String kitName, Set<Material> whitelistedBlocks, String errorMessage) {
            this.kitName = kitName;
            this.whitelistedBlocks = whitelistedBlocks != null ? whitelistedBlocks : new HashSet<>();
            this.errorMessage = errorMessage;
        }

        /**
         * Check if a block type is whitelisted for this kit
         *
         * @param material The material to check
         * @return true if the material is whitelisted
         */
        public boolean isBlockWhitelisted(Material material) {
            return whitelistedBlocks.contains(material);
        }

        /**
         * Add a block type to the whitelist for this kit
         *
         * @param material The material to add
         */
        public void addWhitelistedBlock(Material material) {
            whitelistedBlocks.add(material);
        }

        /**
         * Remove a block type from the whitelist for this kit
         *
         * @param material The material to remove
         */
        public void removeWhitelistedBlock(Material material) {
            whitelistedBlocks.remove(material);
        }

        /**
         * Set the error message for this kit
         *
         * @param message The error message
         */
        public void setErrorMessage(String message) {
            this.errorMessage = message;
        }

        /**
         * Get the formatted error message for this kit
         *
         * @return The formatted error message
         */
        public String getFormattedErrorMessage() {
            return CC.color(errorMessage);
        }
    }
} 