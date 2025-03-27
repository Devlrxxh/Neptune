package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.KitRulesMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalProtectionRadiusButton extends Button {
    private final Kit kit;
    private static final Map<UUID, ChatListener> activeListeners = new HashMap<>();

    public PortalProtectionRadiusButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        // Enable the rule if it's not already
        if (!kit.is(KitRule.PORTAL_PROTECTION_RADIUS)) {
            kit.toggle(KitRule.PORTAL_PROTECTION_RADIUS);
        }
        
        // Close the inventory to allow chat input
        player.closeInventory();
        
        // Send instruction message
        player.sendMessage(CC.color("&aPlease enter the portal protection radius (0-10):"));
        player.sendMessage(CC.color("&7Set to 0 to disable protection, or 1-10 to set protection radius."));
        
        // Create a chat listener for this player
        if (activeListeners.containsKey(player.getUniqueId())) {
            // Clean up any existing listeners
            activeListeners.get(player.getUniqueId()).cleanup();
        }
        
        ChatListener chatListener = new ChatListener(player, kit);
        activeListeners.put(player.getUniqueId(), chatListener);
        Bukkit.getPluginManager().registerEvents(chatListener, Neptune.get());
        
        // Save the changes
        KitService.get().saveKits();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        String radiusText = String.valueOf(kit.getPortalProtectionRadius());
        boolean isDisabled = kit.getPortalProtectionRadius() <= 0;
        
        if (kit.is(KitRule.PORTAL_PROTECTION_RADIUS)) {
            return new ItemBuilder(Material.BARRIER)
                    .name("&a" + KitRule.PORTAL_PROTECTION_RADIUS.getName())
                    .lore(
                            "&7" + KitRule.PORTAL_PROTECTION_RADIUS.getDescription(),
                            "&7",
                            "&aPortal Protection: " + (isDisabled ? "&cDisabled" : "&aEnabled"),
                            isDisabled ? 
                                "&7Protection radius: &e0 &7(disabled)" : 
                                "&aProtection radius: &e" + radiusText + " &ablocks",
                            "&7",
                            "&eClick to " + (isDisabled ? "enable" : "change radius")
                    )
                    .build();
        } else {
            return new ItemBuilder(Material.BARRIER)
                    .name("&c" + KitRule.PORTAL_PROTECTION_RADIUS.getName())
                    .lore(
                            "&7" + KitRule.PORTAL_PROTECTION_RADIUS.getDescription(),
                            "&7",
                            "&cPortal Protection rule disabled",
                            "&7Radius: &e" + radiusText + " &7blocks (inactive)",
                            "&7",
                            "&eClick to enable rule"
                    )
                    .build();
        }
    }
    
    /**
     * Listener class for handling chat input for portal protection radius
     */
    private static class ChatListener implements Listener {
        private final UUID playerUuid;
        private final Kit kit;
        private BukkitTask timeoutTask;
        
        public ChatListener(Player player, Kit kit) {
            this.playerUuid = player.getUniqueId();
            this.kit = kit;
            
            // Set a timeout to cancel after 20 seconds
            this.timeoutTask = new BukkitRunnable() {
                @Override
                public void run() {
                    Player p = Bukkit.getPlayer(playerUuid);
                    if (p != null && p.isOnline()) {
                        p.sendMessage(CC.color("&cTime expired. Using previous value."));
                        // Reopen the menu
                        new KitRulesMenu(kit).open(p);
                    }
                    cleanup();
                }
            }.runTaskLater(Neptune.get(), 20 * 20); // 20 seconds timeout
        }
        
        @EventHandler
        public void onChat(AsyncPlayerChatEvent event) {
            if (!event.getPlayer().getUniqueId().equals(playerUuid)) {
                return;
            }
            
            event.setCancelled(true);
            String message = event.getMessage();
            
            // Process on the main thread
            Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                Player player = event.getPlayer();
                if (!player.isOnline()) {
                    cleanup();
                    return;
                }
                
                // Try to parse the input as a number
                try {
                    int radius = Integer.parseInt(message);
                    
                    if (radius < 0 || radius > 10) {
                        player.sendMessage(CC.color("&cInvalid number! Please enter a number between 0 and 10:"));
                        return;
                    }
                    
                    // Set the portal protection radius value
                    kit.setPortalProtectionRadius(radius);
                    KitService.get().saveKits();
                    
                    if (radius == 0) {
                        player.sendMessage(CC.color("&aPortal protection has been &cdisabled&a!"));
                    } else {
                        player.sendMessage(CC.color("&aSuccessfully set portal protection radius to &e" + radius + " &ablocks!"));
                    }
                    
                    // Reopen the menu
                    new KitRulesMenu(kit).open(player);
                    cleanup();
                } catch (NumberFormatException e) {
                    player.sendMessage(CC.color("&cInvalid input! Please enter a number between 0 and 10:"));
                }
            });
        }
        
        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            if (event.getPlayer().getUniqueId().equals(playerUuid)) {
                cleanup();
            }
        }
        
        public void cleanup() {
            if (timeoutTask != null) {
                timeoutTask.cancel();
                timeoutTask = null;
            }
            
            HandlerList.unregisterAll(this);
            activeListeners.remove(playerUuid);
        }
    }
} 