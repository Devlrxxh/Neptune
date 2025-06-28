package dev.lrxh.neptune.events;

import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class MatchParticipantDeathEvent extends Event implements Cancellable {
  @Getter private final Match match;
  @Getter private final Participant participant;
  @Setter private boolean cancelled;

  public MatchParticipantDeathEvent(Match match, Participant participant) {
    this.match = match;
    this.participant = participant;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  private static final HandlerList handlers = new HandlerList();
  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
