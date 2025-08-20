package dev.lrxh.api.match;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.match.participant.IParticipant;

import java.util.List;
import java.util.UUID;

public interface IMatch {

    List<UUID> getSpectators();
    UUID getUuid();

    IMatchState getState();
    IArena getArena();
    IKit getKit();
    List<IParticipant> getParticipant();
    int getRounds();
    boolean isDuel();
    boolean isEnded();

    void broadcast(String message);
}
