package dev.lrxh.neptune.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@CommandAlias("kit")
@CommandPermission("neptune.admin.kit")
@Description("Command to manage and create new kits.")
public class KitCommand extends BaseCommand {
    private final Neptune plugin = Neptune.get();

    @Subcommand("list")
    public void list(Player player) {
        if (player == null)
            return;

        player.sendMessage(CC.translate("&7&m----------------------------------"));
        player.sendMessage(CC.translate("&9Kits: "));
        player.sendMessage(" ");
        plugin.getKitManager().kits.forEach(kit -> player.sendMessage(CC.translate("&7- &9" + kit.getDisplayName() + " &7 | " + kit.getDisplayName())));
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
                .build();


        plugin.getKitManager().kits.add(kit);
        plugin.getKitManager().saveKits();
        player.sendMessage(CC.translate("&aSuccessfully created a new kit!"));
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

    private boolean checkKit(String kitName) {
        return plugin.getKitManager().getKitByName(kitName) != null;
    }
}