package dev.lrxh.neptune.queue.events;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.Queue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class QueueJoinEvent  extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Queue queue;
    private final UUID uuid;

    public QueueJoinEvent(UUID uuid, Queue queue) {
        this.queue = queue;
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Kit getKit() {
        return queue.getKit();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
