package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.Neptune;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@NoArgsConstructor
public abstract class Button {
    public Neptune plugin = Neptune.get();
    private boolean sound = true;

    public void onClick(Player player, ClickType clickType) {
        sound = false;
    }

    public abstract ItemStack getButtonItem(Player player);

    public boolean isDisplay() {
        return false;
    }
}
