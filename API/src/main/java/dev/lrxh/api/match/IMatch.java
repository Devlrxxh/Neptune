package dev.lrxh.api.match;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.utils.ITime;

import java.util.List;
import java.util.UUID;

public interface IMatch {

    List<UUID> getSpectators();
    UUID getUuid();

    IMatchState getState();
    IArena getArena();
    IKit getKit();
    List<IParticipant> getParticipants();
    int getRounds();
    boolean isDuel();
    boolean isEnded();
    ITime getTime();

    void broadcast(String message);
}
