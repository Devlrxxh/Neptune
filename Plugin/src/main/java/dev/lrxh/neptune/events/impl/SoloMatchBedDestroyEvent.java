package dev.lrxh.neptune.events.impl;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;

public class SoloMatchBedDestroyEvent extends Event {
  @Getter private final Match match;
  @Getter private final Participant bedOwner;
  @Getter private final Participant bedBreaker;

  public SoloMatchBedDestroyEvent(Match match, Participant bedOwner, Participant bedBreaker) {
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
