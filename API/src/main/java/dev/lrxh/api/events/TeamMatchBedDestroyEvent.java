package dev.lrxh.api.events;


import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.match.team.IMatchTeam;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamMatchBedDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IMatch match;
    @Getter
    private final IMatchTeam bedOwner;
    @Getter
    private final IParticipant bedBreaker;

    public TeamMatchBedDestroyEvent(IMatch match, IMatchTeam bedOwner, IParticipant bedBreaker) {
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
