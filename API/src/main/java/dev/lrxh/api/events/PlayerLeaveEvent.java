package dev.lrxh.api.events;

import dev.lrxh.api.match.IMatch;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final String previousStatus;

    public PlayerLeaveEvent(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
