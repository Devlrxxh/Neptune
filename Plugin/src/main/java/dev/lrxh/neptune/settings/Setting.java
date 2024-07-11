package dev.lrxh.neptune.settings;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.profile.Profile;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@Getter
@SuppressWarnings("all")
public enum Setting {
    PLAYER_VISIBILITY(MenusLocale.SETTINGS_PLAYER_VISIBILITY_TITLE.getString(),
            MenusLocale.SETTINGS_PLAYER_VISIBILITY_MATERIAL.getString(),
            MenusLocale.SETTINGS_PLAYER_VISIBILITY_LORE_ENABLED.getStringList(),
            MenusLocale.SETTINGS_PLAYER_VISIBILITY_LORE_DISABLED.getStringList(),
            MenusLocale.SETTINGS_PLAYER_VISIBILITY_SLOT.getInt()) {
        @Override
        public void execute(Player player, ClickType clicktype) {
            Profile profile = getProfile(player);
            profile.getSettingData().setPlayerVisibility(!profile.getSettingData().isPlayerVisibility());
            profile.handleVisibility();
        }

        @Override
        public boolean toggled(Player player) {
            return getProfile(player).getSettingData().isPlayerVisibility();
        }
    },
    ALLOW_SPECTATORS(MenusLocale.SETTINGS_ALLOW_SPECTATORS_TITLE.getString(),
            MenusLocale.SETTINGS_ALLOW_SPECTATORS_MATERIAL.getString(),
            MenusLocale.SETTINGS_ALLOW_SPECTATORS_LORE_ENABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_SPECTATORS_LORE_DISABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_SPECTATORS_SLOT.getInt()) {
        @Override
        public void execute(Player player, ClickType clicktype) {
            Profile profile = getProfile(player);
            profile.getSettingData().setAllowSpectators(!profile.getSettingData().isAllowSpectators());
        }

        @Override
        public boolean toggled(Player player) {
            return getProfile(player).getSettingData().isAllowSpectators();
        }
    },
    ALLOW_DUEL_REQUESTS(MenusLocale.SETTINGS_ALLOW_DUEL_REQUESTS_TITLE.getString(),
            MenusLocale.SETTINGS_ALLOW_DUEL_REQUESTS_MATERIAL.getString(),
            MenusLocale.SETTINGS_ALLOW_DUEL_REQUESTS_LORE_ENABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_DUEL_REQUESTS_LORE_DISABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_DUEL_REQUESTS_SLOT.getInt()) {
        @Override
        public void execute(Player player, ClickType clicktype) {
            Profile profile = getProfile(player);
            profile.getSettingData().setAllowDuels(!profile.getSettingData().isAllowDuels());
        }

        @Override
        public boolean toggled(Player player) {
            return getProfile(player).getSettingData().isAllowDuels();
        }
    },
    ALLOW_PARTY_REQUESTS(MenusLocale.SETTINGS_ALLOW_PARTY_REQUESTS_TITLE.getString(),
            MenusLocale.SETTINGS_ALLOW_PARTY_REQUESTS_MATERIAL.getString(),
            MenusLocale.SETTINGS_ALLOW_PARTY_REQUESTS_LORE_ENABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_PARTY_REQUESTS_LORE_DISABLED.getStringList(),
            MenusLocale.SETTINGS_ALLOW_PARTY_REQUESTS_SLOT.getInt()) {
        @Override
        public void execute(Player player, ClickType clicktype) {
            Profile profile = getProfile(player);
            profile.getSettingData().setAllowParty(!profile.getSettingData().isAllowParty());
        }

        @Override
        public boolean toggled(Player player) {
            return getProfile(player).getSettingData().isAllowParty();
        }
    },
    PING_RANGE(MenusLocale.SETTINGS_PING_RANGE_TITLE.getString(),
            MenusLocale.SETTINGS_PING_RANGE_MATERIAL.getString(),
            MenusLocale.SETTINGS_PING_RANGE_LORE.getStringList(),
            MenusLocale.SETTINGS_PING_RANGE_SLOT.getInt()) {
        @Override
        public void execute(Player player, ClickType clicktype) {
            Profile profile = getProfile(player);

            if (clicktype.equals(ClickType.LEFT)) {
                profile.getSettingData().increasePing();
            } else if (clicktype.equals(ClickType.RIGHT)) {
                profile.getSettingData().decreasePing();
            }

        }
    };

    private String displayName;
    private String material;
    private List<String> enabledLore;
    private List<String> disabledLore;
    private int slot;

    Setting(String displayName, String material, List<String> enabledLore, List<String> disabledLore, int slot) {
        this.displayName = displayName;
        this.material = material;
        this.enabledLore = enabledLore;
        this.disabledLore = disabledLore;
        this.slot = slot;
    }

    Setting(String displayName, String material, List<String> lore, int slot) {
        this.displayName = displayName;
        this.material = material;
        this.enabledLore = lore;
        this.slot = slot;
    }

    public Profile getProfile(Player player) {
        return Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
    }

    public abstract void execute(Player player, ClickType clickType);

    public boolean toggled(Player player) {
        return true;
    }
}
