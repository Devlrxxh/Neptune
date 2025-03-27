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

public class CustomRoundsButton extends Button {
    private final Kit kit;
    private static final Map<UUID, ChatListener> activeListeners = new HashMap<>();

    public CustomRoundsButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        // Enable the rule if it's not already
        if (!kit.is(KitRule.BEST_OF_ROUNDS)) {
            kit.toggle(KitRule.BEST_OF_ROUNDS);
        }
        
        // Close the inventory to allow chat input
        player.closeInventory();
        
        // Send instruction message
        player.sendMessage(CC.color("&aPlease enter the number of rounds required to win (1-10):"));
        player.sendMessage(CC.color("&7Setting to 1 will disable Best of mode while keeping the setting enabled."));
        
        // Create a chat listener for this player
        if (activeListeners.containsKey(player.getUniqueId())) {
            // Clean up any existing listeners
            activeListeners.get(player.getUniqueId()).cleanup();
        }
        
        ChatListener chatListener = new ChatListener(player, kit);
        activeListeners.put(player.getUniqueId(), chatListener);
        Bukkit.getPluginManager().registerEvents(chatListener, Neptune.get());
        
        // Set default value if not set
        if (kit.getCustomRounds() <= 0) {
            kit.setCustomRounds(3);
        }
        
        // Save the changes
        KitService.get().saveKits();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        String roundsText = String.valueOf(kit.getCustomRounds());
        
        if (kit.is(KitRule.BEST_OF_ROUNDS)) {
            return new ItemBuilder(Material.GOLDEN_AXE)
                    .name("&a" + KitRule.BEST_OF_ROUNDS.getName())
                    .lore(
                            "&7" + KitRule.BEST_OF_ROUNDS.getDescription(),
                            "&7",
                            "&aBest of matches enabled",
                            "&aRounds to win: &e" + roundsText,
                            "&7",
                            "&eClick to change rounds"
                    )
                    .build();
        } else {
            return new ItemBuilder(Material.GOLDEN_AXE)
                    .name("&c" + KitRule.BEST_OF_ROUNDS.getName())
                    .lore(
                            "&7" + KitRule.BEST_OF_ROUNDS.getDescription(),
                            "&7",
                            "&cBest of matches disabled",
                            "&7Rounds: &e" + roundsText + " &7(inactive)",
                            "&7",
                            "&eClick to enable and set rounds"
                    )
                    .build();
        }
    }
    
    /**
     * Listener class for handling chat input for custom rounds
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
                    int rounds = Integer.parseInt(message);
                    
                    if (rounds < 1 || rounds > 10) {
                        player.sendMessage(CC.color("&cInvalid number! Please enter a number between 1 and 10:"));
                        return;
                    }
                    
                    // Set the custom rounds value
                    kit.setCustomRounds(rounds);
                    KitService.get().saveKits();
                    
                    player.sendMessage(CC.color("&aSuccessfully set rounds to win to &e" + rounds + "&a!"));
                    
                    // Reopen the menu
                    new KitRulesMenu(kit).open(player);
                    cleanup();
                } catch (NumberFormatException e) {
                    player.sendMessage(CC.color("&cInvalid input! Please enter a number between 1 and 10:"));
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