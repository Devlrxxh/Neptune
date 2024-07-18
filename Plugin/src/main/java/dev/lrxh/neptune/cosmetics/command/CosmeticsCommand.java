package dev.lrxh.neptune.cosmetics.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.lrxh.neptune.cosmetics.menu.CosmeticsManageMenu;
import dev.lrxh.neptune.cosmetics.menu.killEffects.KillEffectsMenu;
import dev.lrxh.neptune.cosmetics.menu.killMessages.KillMessagesMenu;
import org.bukkit.entity.Player;

@CommandAlias("queue")
@Description("Queue Selection command.")
public class CosmeticsCommand extends BaseCommand {

    @Default
    public void cosmeticsMenu(Player player) {
        new CosmeticsManageMenu().openMenu(player.getUniqueId());
    }

    @Subcommand("killEffects")
    public void killEffects(Player player) {
        new KillEffectsMenu().openMenu(player.getUniqueId());
    }

    @Subcommand("killMessages")
    public void killMessages(Player player) {
        new KillMessagesMenu().openMenu(player.getUniqueId());
    }
}
