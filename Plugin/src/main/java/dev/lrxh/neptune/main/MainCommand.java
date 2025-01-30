package dev.lrxh.neptune.main;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.knockback.KnockBack;
import dev.lrxh.knockback.KnockbackController;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.cosmetics.CosmeticService;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MainCommand {

    @Command(name = "", desc = "")
    @Require("neptune.admin")
    public void help(@Sender Player player) {
        new MainMenu().open(player);
    }

    @Command(name = "setspawn", desc = "")
    @Require("neptune.admin")
    public void setspawn(@Sender Player player) {
        Neptune.get().getCache().setSpawn(player.getLocation());
        player.sendMessage(CC.color("&aSuccessfully set spawn!"));
    }

    @Command(name = "setkb", desc = "")
    public void setkb(@Sender Player player, double horzi, double vert, double horzi2, double vert2) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            KnockbackController.getInstance().add(p, new KnockBack(horzi, vert, horzi2, vert2));
        }
    }

    @Command(name = "reload", desc = "")
    @Require("neptune.admin")
    public void reload(@Sender Player player) {
        ConfigService.get().load();
        CosmeticService.get().load();
        HotbarService.get().loadItems();

        for (Player p : Bukkit.getOnlinePlayers()) {
            HotbarService.get().giveItems(p);
        }

        player.sendMessage(CC.color("&aSuccessfully reloaded configs!"));
    }
}