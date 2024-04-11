package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

@CommandAlias("kit")
@CommandPermission("neptune.admin.kit")
@Description("Command to manage and create new kits.")
public class KitCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;
        if (plugin.getKitManager().kits.isEmpty()) {
            player.sendMessage(CC.error("No kits found!"));
            return;
        }
        player.sendMessage(CC.translate("&7&m----------------------------------"));
        player.sendMessage(CC.translate("&9Kits: "));
        player.sendMessage(" ");
        plugin.getKitManager().kits.forEach(kit -> player.sendMessage(CC.translate("&7- &9" + kit.getName() + " &7 | " + kit.getDisplayName())));
        player.sendMessage(CC.translate("&7&m----------------------------------"));
    }

    @Subcommand("create")
    @Syntax("<kitName>")
    public void create(Player player, String kitName) {
        if (player == null) return;
        if (checkKit(kitName)) {
            player.sendMessage(CC.error("Kit already exists!"));
            return;
        }

        Kit kit = Kit.builder()
                .displayName("&9" + kitName)
                .name(kitName)
                .items(Arrays.asList(player.getInventory().getContents()))
                .armour(Arrays.asList(player.getInventory().getArmorContents()))
                .ranked(false)
                .build(false)
                .hunger(false)
                .sumo(false)
                .fallDamage(false)
                .denyMovement(false)
                .arenas(new HashSet<>())
                .build();


        plugin.getKitManager().kits.add(kit);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully created new Kit!"));
    }

    @Subcommand("getinv")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void getinv(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.getInventory().setArmorContents(kit.getArmour().toArray(new ItemStack[0]));

        player.sendMessage(CC.translate("&aSuccessfully given kit load out!"));
    }


    @Subcommand("setinv")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void setinv(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.setItems(Arrays.asList(player.getInventory().getContents()));
        kit.setArmour(Arrays.asList(player.getInventory().getArmorContents()));
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully set kit load out!"));
    }

    @Subcommand("ranked")
    @Syntax("<kit>")
    @CommandCompletion("@kits")
    public void ranked(Player player, String kitName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);

        kit.setRanked(!kit.isRanked());
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully set kit ranked status to " + (kit.isRanked() ? "&aTrue" : "&cFalse") + "!"));
    }

    @Subcommand("addArena")
    @Syntax("<kit> <arena>")
    @CommandCompletion("@kits @arenas")
    public void addArena(Player player, String kitName, String arenaName) {
        if (player == null) return;
        if (!checkKit(kitName)) {
            player.sendMessage(CC.error("Kit doesn't exist!"));
            return;
        }

        if (plugin.getArenaManager().getArenaByName(arenaName) == null) {
            player.sendMessage(CC.error("Arena doesn't exist!"));
            return;
        }
        Kit kit = plugin.getKitManager().getKitByName(kitName);
        Arena arena = plugin.getArenaManager().getArenaByName(arenaName);

        kit.getArenas().add(arena);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully added arena &7(" + arena.getDisplayName() + "&7) for kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    private boolean checkKit(String kitName) {
        return plugin.getKitManager().getKitByName(kitName) != null;
    }
}