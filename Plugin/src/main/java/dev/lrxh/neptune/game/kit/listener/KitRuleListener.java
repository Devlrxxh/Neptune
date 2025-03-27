package dev.lrxh.neptune.game.kit.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitRuleListener implements Listener {

    private final Map<UUID, Long> arrowCooldowns = new HashMap<>();
    private static final long ARROW_COOLDOWN_MILLIS = 5000; // 5 seconds cooldown
    private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.#");
    
    @EventHandler
    public void onGoldenAppleConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Check if this is a golden apple
        if (item.getType() != Material.GOLDEN_APPLE) {
            return;
        }
        
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        
        Match match = profile.getMatch();
        if (match == null) return;
        
        // Check if the kit has the instant golden apple healing rule enabled
        if (match.getKit().is(KitRule.INSTANT_GAPPLE_HEAL)) {
            // Set player's health to maximum
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            player.setHealth(maxHealth);
            
            // Play healing effect
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.2f);
            player.sendMessage(CC.color("&a&l➤ &aInstant healing applied!"));
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onArrowHit(ProjectileHitEvent event) {
        // Check if this is an arrow
        if (!(event.getEntity() instanceof Arrow arrow)) {
            return;
        }
        
        // Make sure the arrow has a shooter
        if (!(arrow.getShooter() instanceof Player shooter)) {
            return;
        }
        
        // Check if arrow hit a player
        if (event.getHitEntity() instanceof Player target && target != shooter) {
            // Get profiles for both players
            Profile shooterProfile = API.getProfile(shooter.getUniqueId());
            if (shooterProfile == null || shooterProfile.getMatch() == null) return;
            
            // Format health display message
            double health = target.getHealth();
            String healthDisplay = HEALTH_FORMAT.format(health);
            
            // Send message to shooter
            shooter.sendMessage(CC.color("&e" + target.getName() + " &7has &c" + healthDisplay + " &c❤ &7health remaining."));
        }
        
        // Remove the arrow from the world to prevent pickup
        // Add a small delay to ensure hit mechanics are processed first
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isValid()) {
                    arrow.remove();
                }
            }
        }.runTaskLater(Neptune.get(), 2L);
    }
    
    // Prevent any arrow pickup for match players
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrowPickup(EntityPickupItemEvent event) {
        // Check if it's a player picking up
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        // Check if they're in a match
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null || profile.getMatch() == null) return;
        
        // Check if the item is an arrow
        if (event.getItem().getItemStack().getType() == Material.ARROW) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        
        if (!(event.getProjectile() instanceof Arrow arrow)) {
            return;
        }
        
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        
        Match match = profile.getMatch();
        if (match == null) return;
        
        // Make arrows non-pickable
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        
        // Schedule arrow removal after 30 seconds (to prevent world clutter)
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isValid()) {
                    arrow.remove();
                }
            }
        }.runTaskLater(Neptune.get(), 30 * 20L); // 30 seconds
        
        // Check if the kit has the infinite arrows rule enabled
        if (match.getKit().is(KitRule.INFINITE_ARROWS)) {
            // Check if player is on cooldown
            if (arrowCooldowns.containsKey(player.getUniqueId())) {
                long timeLeft = (arrowCooldowns.get(player.getUniqueId()) + ARROW_COOLDOWN_MILLIS) - System.currentTimeMillis();
                if (timeLeft > 0) {
                    // Still on cooldown, do nothing
                    return;
                }
            }
            
            // Set cooldown
            arrowCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
            
            // Start XP bar visualization for cooldown
            // Save original XP values to restore later
            float originalExp = player.getExp();
            int originalLevel = player.getLevel();
            
            // Create a task that updates every second to show the countdown
            new BukkitRunnable() {
                private int secondsLeft = 5;
                
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        cancel();
                        return;
                    }
                    
                    // Make sure the match is still active
                    Profile currentProfile = API.getProfile(player);
                    if (currentProfile == null || currentProfile.getMatch() == null) {
                        cancel();
                        // Restore original XP values
                        player.setExp(originalExp);
                        player.setLevel(originalLevel);
                        return;
                    }
                    
                    if (secondsLeft <= 0) {
                        // Time's up - give arrow and reset XP
                        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 2.0f);
                        player.sendMessage(CC.color("&a&l➤ &aArrow replenished!"));
                        
                        // Reset XP display to original
                        player.setExp(originalExp);
                        player.setLevel(originalLevel);
                        
                        cancel();
                        return;
                    }
                    
                    // Update XP bar display
                    player.setLevel(secondsLeft);
                    player.setExp((float) secondsLeft / 5.0f); // From 1.0 to 0.0
                    
                    // Play tick sound every second
                    if (secondsLeft <= 3) {
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    }
                    
                    secondsLeft--;
                }
            }.runTaskTimer(Neptune.get(), 0L, 20L); // Update every second
        }
    }
} 