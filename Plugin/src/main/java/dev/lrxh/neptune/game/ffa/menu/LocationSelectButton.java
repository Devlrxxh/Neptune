package dev.lrxh.neptune.game.ffa.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.ffa.FFAArena;
import dev.lrxh.neptune.game.ffa.FFAService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class LocationSelectButton extends Button {

    private FFAArena arena;
    private String locationName;
    private Kit kit;

    public LocationSelectButton(int slot, FFAArena arena, String locationName, Kit kit) {
        super(slot);
        this.arena = arena;
        this.locationName = locationName;
        this.kit = kit;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.DIAMOND)
                .name(MenusLocale.FFA_SPAWN_LOCATION_FORMAT.getString().replace("<spawnName>", locationName)
                        .replace("<arenaName>", arena.getName()))
                .lore(MenusLocale.FFA_SPAWN_LOCATION_LORE.getStringList()).build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        FFAService.get().join(ProfileService.get().getByUUID(player.getUniqueId()), kit, locationName);
    }
}
