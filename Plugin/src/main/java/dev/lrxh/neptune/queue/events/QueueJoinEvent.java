package dev.lrxh.neptune.queue.events;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.queue.QueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class QueueJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final QueueEntry queueEntry;
    private final UUID uuid;
    private boolean cancelled = false;

    public QueueJoinEvent(UUID uuid, QueueEntry queueEntry) {
        this.queueEntry = queueEntry;
        this.uuid = uuid;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Kit getKit() {
        return queueEntry.getKit();
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
}
