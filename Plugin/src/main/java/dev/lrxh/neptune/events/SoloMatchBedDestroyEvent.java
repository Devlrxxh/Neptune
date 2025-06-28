package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SoloMatchBedDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;
    @Getter
    private final Participant bedOwner;
    @Getter
    private final Participant bedBreaker;

    public SoloMatchBedDestroyEvent(Match match, Participant bedOwner, Participant bedBreaker) {
        this.match = match;
        this.bedOwner = bedOwner;
        this.bedBreaker = bedBreaker;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
