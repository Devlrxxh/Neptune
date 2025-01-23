package dev.lrxh.neptune.cosmetics.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CosmeticsManagementButton extends Button {
    private final String title;
    private final List<String> lore;
    private final Material material;
    private final Menu menu;

    public CosmeticsManagementButton(int slot, String title, List<String> lore, Material material, Menu menu) {
        super(slot);
        this.title = title;
        this.lore = lore;
        this.material = material;
        this.menu = menu;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        menu.open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);
        return new ItemBuilder(material).name(title).lore(ItemUtils.getLore(lore, new Replacement("<kill-effect>", profile.getSettingData().getKillEffect().getDisplayName()), new Replacement("<kill-message>", profile.getSettingData().getKillMessagePackage().getDisplayName()
        )), player).clearFlags().build();
    }
}
