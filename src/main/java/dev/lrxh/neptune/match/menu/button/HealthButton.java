package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class HealthButton extends Button {

    private MatchSnapshot snapshot;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(PlayerUtil.getPlayerHead(snapshot.getUuid()))
                .name("&7Player Stats")
                .lore(Arrays.asList(
                        "&8| &7Health: &c" + snapshot.getHealth() + StringEscapeUtils.unescapeJava("❤"),
                        "&8| &7Hunger: &e" + snapshot.getHunger() + "&7/20"
                ))
                .clearFlags()
                .build();
    }

}
