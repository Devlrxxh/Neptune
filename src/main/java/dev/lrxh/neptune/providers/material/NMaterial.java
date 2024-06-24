package dev.lrxh.neptune.providers.material;

import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum NMaterial {
    PLAYER_HEAD {
        @Override
        public ItemStack getItem(Player player) {
            return PlayerUtil.getPlayerHead(player.getUniqueId());
        }
    };

    public abstract ItemStack getItem(Player player);

}