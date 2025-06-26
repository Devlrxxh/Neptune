package dev.lrxh.neptune.events.impl;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;

public class MatchParticipantLeaveEvent extends Event {
  @Getter private final Match match;
  @Getter private final Participant participant;

  public MatchParticipantLeaveEvent(Match match, Participant participant) {
    this.match = match;
    this.participant = participant;
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
