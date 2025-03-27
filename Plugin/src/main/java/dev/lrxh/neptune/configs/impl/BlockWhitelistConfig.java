package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

@Getter
public class BlockWhitelistConfig {

    private static BlockWhitelistConfig instance;
    private final ConfigFile configFile;
    
    private final Map<String, Set<Material>> kitBlockWhitelists = new HashMap<>();
    private final Map<String, String> kitErrorMessages = new HashMap<>();
    
    private final Set<Material> defaultWhitelist = new HashSet<>();
    private String defaultMessage = "&cYou can't break that block here";
    
    public BlockWhitelistConfig() {
        this.configFile = new ConfigFile("blockwhitelist");
        instance = this;
        loadConfig();
    }
    
    public static BlockWhitelistConfig get() {
        return instance;
    }
    
    public void loadConfig() {
        File file = new File(Neptune.get().getDataFolder(), "blockwhitelist.yml");
        
        if (!file.exists()) {
            Neptune.get().saveResource("blockwhitelist.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Clear existing data
        kitBlockWhitelists.clear();
        kitErrorMessages.clear();
        defaultWhitelist.clear();
        
        // Load default configuration
        ConfigurationSection defaultSection = config.getConfigurationSection("default");
        if (defaultSection != null) {
            defaultMessage = defaultSection.getString("message", defaultMessage);
            
            List<String> defaultBlocks = defaultSection.getStringList("blocks");
            for (String materialName : defaultBlocks) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    defaultWhitelist.add(material);
                } catch (IllegalArgumentException e) {
                    Neptune.get().getLogger().log(Level.WARNING, 
                        "Invalid material name in blockwhitelist.yml default section: " + materialName);
                }
            }
        }
        
        // Load kit-specific configurations
        for (String kitName : config.getKeys(false)) {
            if (kitName.equals("default")) continue;
            
            ConfigurationSection kitSection = config.getConfigurationSection(kitName);
            if (kitSection == null) continue;
            
            // Load kit message
            String kitMessage = kitSection.getString("message", defaultMessage);
            kitErrorMessages.put(kitName.toLowerCase(), kitMessage);
            
            // Load kit blocks
            Set<Material> kitBlocks = new HashSet<>();
            List<String> blockList = kitSection.getStringList("blocks");
            
            for (String materialName : blockList) {
                try {
                    Material material = Material.valueOf(materialName.toUpperCase());
                    kitBlocks.add(material);
                } catch (IllegalArgumentException e) {
                    Neptune.get().getLogger().log(Level.WARNING, 
                        "Invalid material name in blockwhitelist.yml for kit " + kitName + ": " + materialName);
                }
            }
            
            // Only add if there are valid blocks
            if (!kitBlocks.isEmpty()) {
                kitBlockWhitelists.put(kitName.toLowerCase(), kitBlocks);
            }
        }
        
        Neptune.get().getLogger().info("Loaded block whitelist configuration with " + 
            kitBlockWhitelists.size() + " kit configurations and " + defaultWhitelist.size() + " default blocks");
    }
    
    /**
     * Creates a default whitelist entry for a kit if one doesn't already exist
     * 
     * @param kitName The name of the kit to create an entry for
     */
    public void createDefaultWhitelistForKit(String kitName) {
        File file = new File(Neptune.get().getDataFolder(), "blockwhitelist.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        
        // Check if entry already exists
        if (config.contains(kitName)) {
            return; // Don't overwrite existing entries
        }
        
        // Create new entry with default blocks
        config.set(kitName + ".blocks", Arrays.asList("RED_TERRACOTTA", "BLUE_TERRACOTTA", "WHITE_TERRACOTTA"));
        config.set(kitName + ".message", "&cYou can only break terracotta blocks with this kit!");
        
        // Save the config
        try {
            config.save(file);
            Neptune.get().getLogger().info("Created default block whitelist for kit: " + kitName);
            
            // Update the runtime maps
            Set<Material> blocks = new HashSet<>();
            blocks.add(Material.RED_TERRACOTTA);
            blocks.add(Material.BLUE_TERRACOTTA);
            blocks.add(Material.WHITE_TERRACOTTA);
            kitBlockWhitelists.put(kitName.toLowerCase(), blocks);
            kitErrorMessages.put(kitName.toLowerCase(), "&cYou can only break terracotta blocks with this kit!");
            
        } catch (Exception e) {
            Neptune.get().getLogger().log(Level.WARNING, "Failed to save block whitelist for kit: " + kitName, e);
        }
    }
    
    /**
     * Check if a block type is whitelisted for the given kit
     * 
     * @param kitName The name of the kit (will be converted to lowercase)
     * @param material The material to check
     * @return true if the material is whitelisted for this kit, false otherwise
     */
    public boolean isWhitelisted(String kitName, Material material) {
        // First check kit-specific list
        Set<Material> kitWhitelist = kitBlockWhitelists.get(kitName.toLowerCase());
        if (kitWhitelist != null && kitWhitelist.contains(material)) {
            return true;
        }
        
        // Fall back to default list
        return defaultWhitelist.contains(material);
    }
    
    /**
     * Get the error message for a kit when breaking non-whitelisted blocks
     * 
     * @param kitName The name of the kit (will be converted to lowercase)
     * @return The formatted error message
     */
    public String getErrorMessage(String kitName) {
        String message = kitErrorMessages.get(kitName.toLowerCase());
        return CC.color(message != null ? message : defaultMessage);
    }
} 