package dev.lrxh.api.events;

import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.queue.IQueueEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QueueJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final IQueueEntry queueEntry;
    private boolean cancelled = false;

    public QueueJoinEvent(IQueueEntry queueEntry) {
        this.queueEntry = queueEntry;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(queueEntry.getUuid());
    }

    public IKit getKit() {
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
