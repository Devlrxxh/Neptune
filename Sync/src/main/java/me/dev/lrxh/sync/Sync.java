package me.dev.lrxh.sync;

import lombok.Getter;
import me.dev.lrxh.sync.game.GameServerManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Sync extends JavaPlugin {
    private static Sync instance;
    private GameServerManager gameServerManager;

    public static Sync get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        gameServerManager = new GameServerManager();
    }

}
