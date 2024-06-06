package dev.lrxh.neptune.match.menu.button;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class SwitchInventoryButton extends Button {

    private String opponent;
    private MatchSnapshot snapshot;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(XMaterial.PAPER)
                .name("&7Press to switch to " + opponent + " inventory.")
                .lore("&aClick to Switch")
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (snapshot.getUsername().equals(opponent)) return;
        player.chat("/viewinv " + snapshot.getOpponent());
    }
}