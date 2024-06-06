package dev.lrxh.neptune.party.menu.buttons;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PartySplitKitButton extends Button {
    private final Kit kit;
    private final Party party;

    @Override
    public ItemStack getButtonItem(Player player) {
        return null;
    }
}
