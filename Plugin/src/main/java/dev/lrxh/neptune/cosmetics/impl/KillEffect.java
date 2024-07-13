package dev.lrxh.neptune.cosmetics.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.CosmeticsLocale;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

@Getter
public enum KillEffect {
    NONE(CosmeticsLocale.NONE_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.NONE_MATERIAL.getString()),
            CosmeticsLocale.NONE_SLOT.getInt()) {
    },
    LIGHTNING(CosmeticsLocale.LIGHTNING_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.LIGHTNING_MATERIAL.getString()),
            CosmeticsLocale.LIGHTNING_SLOT.getInt()) {
        @Override
        public void execute(Player player) {
            Location location = player.getLocation();
            double x = location.getX();
            double y = location.getY() + 2.0;
            double z = location.getZ();
            location.getWorld().strikeLightning(new Location(location.getWorld(), x, y, z));
        }
    },
    FIREWORKS(CosmeticsLocale.FIREWORKS_DISPLAY_NAME.getString(), Material.valueOf(CosmeticsLocale.FIREWORKS_MATERIAL.getString()),
            CosmeticsLocale.FIREWORKS_SLOT.getInt()) {
        @Override
        public void execute(Player player) {
            Location location = player.getLocation();

            Firework firework = location.getWorld().spawn(location, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            FireworkEffect.Builder builder = FireworkEffect.builder()
                    .withColor(Color.RED)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .trail(true)
                    .flicker(false);
            meta.addEffect(builder.build());
            meta.setPower(1);
            firework.setFireworkMeta(meta);
            new NeptuneRunnable() {
                @Override
                public void run() {
                    firework.detonate();
                }
            }.startLater(5, Neptune.get());
        }
    };

    private final String displayName;
    private final Material material;
    private final int slot;

    KillEffect(String displayName, Material material, int slot) {
        this.displayName = displayName;
        this.material = material;
        this.slot = slot;
    }

    public void execute(Player player) {
    }

    public String permission() {
        return "neptune.cosmetics.kill-effects." + this.name().toLowerCase();
    }
}
