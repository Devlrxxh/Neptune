package dev.lrxh.neptune.events;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamMatchBedDestroyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Match match;
    @Getter
    private final MatchTeam bedOwner;
    @Getter
    private final Participant bedBreaker;

    public TeamMatchBedDestroyEvent(Match match, MatchTeam bedOwner, Participant bedBreaker) {
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
