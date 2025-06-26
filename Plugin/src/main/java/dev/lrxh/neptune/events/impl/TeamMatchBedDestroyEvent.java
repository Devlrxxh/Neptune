package dev.lrxh.neptune.events.impl;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import lombok.Getter;

public class TeamMatchBedDestroyEvent extends Event {
  @Getter private final Match match;
  @Getter private final MatchTeam bedOwner;
  @Getter private final Participant bedBreaker;

  public TeamMatchBedDestroyEvent(Match match, MatchTeam bedOwner, Participant bedBreaker) {
    this.match = match;
    this.bedOwner = bedOwner;
    this.bedBreaker = bedBreaker;
  }

  private static final HandlerList handlers = new HandlerList();
  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
