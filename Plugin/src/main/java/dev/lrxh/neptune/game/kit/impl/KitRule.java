package dev.lrxh.neptune.game.kit.impl;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum KitRule {
    BUILD(Material.LAVA_BUCKET, "Allow/Deny Players to place blocks.", "Build", "build"),
    HUNGER(Material.COOKED_BEEF, "Allow/Deny Players losing Hunger", "Hunger", "hunger"),
    SUMO(Material.LEAD, "Allow/Deny Sumo", "Sumo", "sumo"),
    FALL_DAMAGE(Material.DIAMOND, "Allow/Deny Player to take Fall Damage", "Fall Damage", "fallDamage"),
    DENY_MOVEMENT(Material.WATER_BUCKET, "Allow/Deny Movement on match start", "Deny Movement", "denyMovement"),
    BOXING(Material.DIAMOND_CHESTPLATE, "Allow/Deny Boxing", "Boxing", "boxing"),
    ALLOW_ARENA_BREAK(Material.WOODEN_AXE, "Allow Players to break blocks from the arena", "Arena Break", "arenaBreak"),
    DAMAGE(Material.DIAMOND_SWORD, "Allow/Deny Players to take Damage", "Damage", "damage"),
    SATURATION_HEAL(Material.GOLDEN_APPLE, "If Players should regen from saturation", "Saturation Heal", "saturationHeal"),
    SHOW_HP(Material.APPLE, "If players should see their enemies health under their name", "Show Health", "showHP"),
    ALLOW_KIT_EDITOR(Material.WOODEN_AXE, "If players should be able to modify this kit in kiteditor.", "Allow Kit Editor", "allowKitEditor"),
    ALLOW_PARTY(Material.PARROT_SPAWN_EGG, "If players should be able to play this kit in party events.", "Allow Party Events", "allowParty"),
    BED_WARS(Material.RED_BED, "Allow/Deny Bedwars", "Bedwars", "bedwars"),
    SATURATION(Material.ENCHANTED_GOLDEN_APPLE, "Allow/Deny Saturation", "Saturation", "saturation"),
    BRIDGES(Material.END_PORTAL_FRAME, "The Bridges game mode - score points by jumping into opponent's end portal", "Bridges", "bridges"),
    
    // Block breaking rules
    ONLY_BREAK_PLAYER_PLACED(Material.COBBLESTONE, "Only allow breaking blocks placed by players", "Only Break Player Blocks", "onlyBreakPlayerBlocks"),
    LIMITED_BLOCK_BREAK(Material.GRASS_BLOCK, "Only allow breaking specific block types (Red/Blue/White terracotta by default)", "Limited Block Break", "limitedBlockBreak"),
    
    // Item drop and inventory rules
    DISABLE_ITEM_DROP(Material.HOPPER, "Prevent players from manually dropping items from their inventory (doesn't affect block drops)", "Disable Item Drop", "disableItemDrop"),
    RESET_INVENTORY_AFTER_DEATH(Material.CHEST, "Reset player's inventory to default kit on death", "Reset Inventory on Death", "resetInventoryAfterDeath"),
    
    // Additional Bridges configuration options
    RESET_ARENA_AFTER_SCORE(Material.CRAFTING_TABLE, "Reset arena blocks after a point is scored (timer is always applied)", "Reset After Score", "resetAfterScore"),
    RESPAWN_DELAY(Material.CLOCK, "Add a delay before respawning players", "Respawn Delay", "respawnDelay"),
    
    // Combat and gameplay features
    INSTANT_GAPPLE_HEAL(Material.GOLDEN_APPLE, "Immediately regenerate full health when eating a Golden Apple", "Instant Gapple Heal", "instantGappleHeal"),
    INFINITE_ARROWS(Material.ARROW, "Regenerate an arrow 5 seconds after shooting one", "Infinite Arrows", "infiniteArrows"),
    INFINITE_DURABILITY(Material.ANVIL, "Makes all items unbreakable - they will never lose durability", "Infinite Durability", "infiniteDurability"),
    
    // Unified Best of / Custom rounds setting
    BEST_OF_ROUNDS(Material.GOLDEN_AXE, "Enable best-of matches with customizable rounds to win (click to set number)", "Best of Rounds", "bestOfRounds");

    private final String description;
    private final String name;
    private final Material icon;
    private final String saveName;


    KitRule(Material icon, String description, String name, String saveName) {
        this.description = description;
        this.name = name;
        this.saveName = saveName;
        this.icon = icon;
    }
}