package dev.lrxh.neptune.profile.data;

import lombok.Data;

@Data
public class MatchHistory {
    private final boolean won;
    private final String opponentName;
    private String kitName;
    private String arenaName;
}
