package dev.lrxh.neptune.game.match.impl.participant;

import dev.lrxh.api.match.participant.IDeathCause;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.Getter;

@Getter
public enum DeathCause implements IDeathCause {
    DISCONNECT(MessagesLocale.MATCH_DEATH_DISCONNECT),
    KILL(MessagesLocale.MATCH_DEATH_KILLED),
    DIED(MessagesLocale.MATCH_DEATH_DIED),
    VOID(MessagesLocale.MATCH_DEATH_VOID);

    private final MessagesLocale message;

    DeathCause(MessagesLocale message) {
        this.message = message;
    }
}
