package dev.lrxh.neptune.leaderboard.impl;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerEntry {
    private final String username;
    private final int value;
}
