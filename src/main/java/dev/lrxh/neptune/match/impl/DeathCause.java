package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.Getter;

@Getter
public enum DeathCause {
    DISCONNECT(MessagesLocale.MATCH_DEATH_DISCONNECT),
    KILL(MessagesLocale.MATCH_DEATH_KILLED),
    VOID(MessagesLocale.MATCH_DEATH_VOID);

    private final MessagesLocale messagesLocale;

    DeathCause(MessagesLocale messagesLocale) {
        this.messagesLocale = messagesLocale;
    }
}
