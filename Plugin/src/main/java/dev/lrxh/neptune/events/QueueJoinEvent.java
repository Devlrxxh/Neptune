package dev.lrxh.neptune.events;

import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.game.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QueueJoinEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final QueueEntry queueEntry;
    private boolean cancelled = false;

    public QueueJoinEvent(QueueEntry queueEntry) {
        this.queueEntry = queueEntry;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(queueEntry.getUuid());
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
