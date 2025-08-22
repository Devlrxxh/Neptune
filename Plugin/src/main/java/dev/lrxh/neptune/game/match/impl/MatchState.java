package dev.lrxh.neptune.game.match.impl;

import dev.lrxh.api.match.IMatchState;

public enum MatchState implements IMatchState {
    STARTING,
    IN_ROUND,
    ENDING
}
