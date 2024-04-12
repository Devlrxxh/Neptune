package dev.lrxh.neptune.providers.hotbar;

import org.bukkit.entity.Player;

public enum ItemAction {
    UNRANKED() {
        @Override
        public void execute(Player player) {
            player.chat("yesdsadsa");
        }
    };

    public abstract void execute(Player player);
}
