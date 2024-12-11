package me.dev.lrxh.sync.game;

import dev.lrxh.common.GameServer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameServerManager {
    private final List<GameServer> gameServers = new ArrayList<>();

    public void addGameServer(GameServer gameServer) {
        gameServers.add(gameServer);
    }

    public void removeGameServer(GameServer gameServer) {
        gameServers.remove(gameServer);
    }
}
