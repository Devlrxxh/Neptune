package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MatchReadyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;
    @Setter
    private boolean cancelled;

    public MatchReadyEvent(IMatch match) {
        this.match = match;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
