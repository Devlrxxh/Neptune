package dev.lrxh.neptune.cosmetics.command;

import com.jonahseguin.drink.annotation.Command;
import dev.lrxh.neptune.cosmetics.menu.CosmeticsManageMenu;
import dev.lrxh.neptune.cosmetics.menu.killEffects.KillEffectsMenu;
import dev.lrxh.neptune.cosmetics.menu.killMessages.KillMessagesMenu;
import org.bukkit.entity.Player;

public class CosmeticsCommand {

    @Command(name = "", desc = "")
    public void cosmeticsMenu(Player player) {
        new CosmeticsManageMenu().open(player);
    }

    @Command(name = "killEffects", desc = "")
    public void killEffects(Player player) {
        new KillEffectsMenu().open(player);
    }

    @Command(name = "killMessages", desc = "")
    public void killMessages(Player player) {
        new KillMessagesMenu().open(player);
    }
}
