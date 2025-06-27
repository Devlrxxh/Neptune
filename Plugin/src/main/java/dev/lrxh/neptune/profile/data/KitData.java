package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.game.divisions.DivisionService;
import dev.lrxh.neptune.game.divisions.impl.Division;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class KitData {
    private int wins = 0;
    private int losses = 0;
    private int bestStreak = 0;
    private int currentStreak = 0;
    private List<ItemStack> kitLoadout = new ArrayList<>();
    private Division division;
    private int elo = 0;

    public double getKdr() {
        if (losses == 0) return wins;
        double kd = (double) wins / losses;
        BigDecimal bd = new BigDecimal(kd);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean updateElo(boolean won) {
        int min, max;

        if (won) {
            if (elo < 300) {
                min = 15;
                max = 25;
            } else if (elo < 600) {
                min = 10;
                max = 20;
            } else if (elo < 900) {
                min = 5;
                max = 15;
            } else {
                min = 3;
                max = 10;
            }
        } else {
            if (elo < 300) {
                min = -5;
                max = 0;
            } else if (elo < 600) {
                min = -10;
                max = -5;
            } else if (elo < 900) {
                min = -20;
                max = -10;
            } else {
                min = -25;
                max = -15;
            }
        }

        int change = ThreadLocalRandom.current().nextInt(min, max + 1);

        elo += change;

        if (elo < 0) elo = 0;

        return updateDivision();
    }

    public boolean updateDivision() {
        Division previous = this.division;
        Division updated = DivisionService.get().getDivisionByElo(elo);
        if (updated == null) {
            updated = DivisionService.get().getDivisionByElo(0);
        }
        this.division = updated;

        if (previous == null) return true;

        int previousDivision = previous.getEloRequired();
        int newDivision = updated.getEloRequired();

        return newDivision > previousDivision;
    }
}

