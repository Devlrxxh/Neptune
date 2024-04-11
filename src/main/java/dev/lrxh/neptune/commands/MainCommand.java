package dev.lrxh.neptune.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;


@CommandAlias("neptune")
@CommandPermission("neptune.admin.main")
@Description("Main Command for Neptune Practice Core.")
public class MainCommand extends BaseCommand {

    @Default
    @Subcommand("help")
    public void help(Player player) {
        player.sendMessage(CC.translate("&7&m-----------------------------------------"));
        player.sendMessage(CC.translate("&9" + Neptune.get().getName() + " Core"));
        player.sendMessage(" ");
        player.sendMessage(CC.translate("&7* &9/neptune setspawn &7- &fSet server spawn"));
        player.sendMessage(CC.translate("&7* &9/neptune reload &7- &fReload all configs"));
        player.sendMessage(" ");
        player.sendMessage(CC.translate("&7&m-----------------------------------------"));
    }

    @Subcommand("setspawn")
    public void setspawn(Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        Neptune.get().getCache().save();
        player.sendMessage(CC.translate("&aSuccessfully set spawn!"));
    }


    @Subcommand("reload")
    public void reload(Player player) {
        Neptune.get().loadConfigs();
        player.sendMessage(CC.translate("&aSuccessfully reloaded configs!"));
    }
}