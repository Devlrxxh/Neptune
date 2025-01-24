package dev.lrxh.neptune.kit.command;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.database.DatabaseService;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitCommand {

    @Command(name = "list", desc = "")
    public void list(@Sender Player player) {
        if (player == null)
            return;
        if (KitService.get().kits.isEmpty()) {
            player.sendMessage(CC.error("No kits found!"));
            return;
        }
        player.sendMessage(CC.color("&7&m----------------------------------"));
        player.sendMessage(CC.color("&9Kits: "));
        player.sendMessage(" ");
        KitService.get().kits.forEach(kit -> player.sendMessage(CC.color("&7- &9" + kit.getName() + " &7 | " + kit.getDisplayName())));
        player.sendMessage(CC.color("&7&m----------------------------------"));
    }

    @Command(name = "manage", desc = "", usage = "<kit>")
    public void manage(@Sender Player player, Kit kit) {
        new KitManagementMenu(kit).open(player);
    }

    @Command(name = "create", desc = "", usage = "<name>")
    public void create(@Sender Player player, String kitName) {
        if (checkKit(kitName)) {
            player.sendMessage(CC.error("Kit already exists!"));
            return;
        }

        Kit kit = new Kit(kitName, Arrays.asList(player.getInventory().getContents()), player.getInventory().getItemInMainHand());

        KitService.get().kits.add(kit);
        KitService.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully created new Kit!"));
    }

    @Command(name = "getinv", desc = "", usage = "<kit>")
    public void getinv(@Sender Player player, Kit kit) {
        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.sendMessage(CC.color("&aSuccessfully given kit load out!"));
    }


    @Command(name = "setinv", desc = "", usage = "<kit>")
    public void setinv(@Sender Player player, Kit kit) {
        kit.setItems(Arrays.asList(player.getInventory().getContents()));

        KitService.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully set kit load out!"));
        player.sendMessage(CC.color("&4IMPORTANT &8- &cMake sure to run /kit updateDB " + kit.getName()));
    }


    @Command(name = "rename", desc = "", usage = "<kit> <name>")
    public void rename(@Sender Player player, Kit kit, String name) {
        kit.setDisplayName(name);

        KitService.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully renamed kit display name!"));
    }


    @Command(name = "delete", desc = "", usage = "<kit>")
    public void delete(@Sender Player player, Kit kit) {
        kit.delete();

        KitService.get().saveKits();

        player.sendMessage(CC.color("&aSuccessfully deleted kit!"));
    }

    @Command(name = "updateDB", desc = "", usage = "<kit>")
    public void updateDB(@Sender Player player, Kit kit) {
        int i = 0;
        for (DataDocument document : DatabaseService.get().getDatabase().getAll()) {
            DataDocument kitStatistics = document.getDataDocument("kitData");
            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());

            kitDocument.put("kit", "");
            i++;

            kitStatistics.put("kitData", kitDocument);

            DatabaseService.get().getDatabase().replace(document.getString("uuid"), document);
        }
        ServerUtils.info("Updated kit for " + i + " players!");
    }

    @Command(name = "setIcon", desc = "", usage = "<kit>")
    public void setIcon(@Sender Player player, Kit kit) {
        kit.setIcon(player.getInventory().getItemInMainHand());

        KitService.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit icon!"));
    }

    @Command(name = "setSlot", desc = "", usage = "<kit> <slot>")
    public void setSlot(@Sender Player player, Kit kit, int slot) {
        kit.setSlot(slot);

        KitService.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully set kit slot!"));
    }

    @Command(name = "setIcon", desc = "", usage = "<kit> <arena>")
    public void addArena(@Sender Player player, Kit kit, Arena arena) {
        if (kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena is already added!"));
            return;
        }

        kit.getArenas().add(arena);

        KitService.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully added arena &7(" + arena.getDisplayName() + "&7) for kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    @Command(name = "removeArena", desc = "", usage = "<kit> <arena>")
    public void removeArena(@Sender Player player, Kit kit, Arena arena) {
        if (!kit.getArenas().contains(arena)) {
            player.sendMessage(CC.error("Arena isn't added to the kit!"));
            return;
        }

        kit.getArenas().remove(arena);
        KitService.get().saveKits();
        player.sendMessage(CC.color("&aSuccessfully removed arena &7(" + arena.getDisplayName() + "&7) from kit &7(" + kit.getDisplayName() + "&7)!"));
    }

    private boolean checkKit(String kitName) {
        return KitService.get().getKitByName(kitName) != null;
    }
}