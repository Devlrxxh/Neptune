package dev.lrxh.neptune.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class TtlAction {
    private final UUID playerUUID;
    private final Consumer<Player> consumer;
}
