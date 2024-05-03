package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.WeakHashMap;

@Getter
@Setter
public class PlayerData {
    private WeakHashMap<Kit, KitData> kitData;
    private DuelRequest duelRequest;

    public PlayerData() {
        this.kitData = new WeakHashMap<>();
    }

    public void run(Kit kit, boolean won) {
        if (won) {
            addWin(kit);
        } else {
            addLoss(kit);
            kitData.get(kit).setCurrentStreak(0);
        }
    }

    public void addWin(Kit kit) {
        kitData.get(kit).setWins(kitData.get(kit).getWins() + 1);
        addWinStreak(kit);
    }

    public void addLoss(Kit kit) {
        kitData.get(kit).setLosses(kitData.get(kit).getLosses() + 1);
    }

    public void addWinStreak(Kit kit) {
        kitData.get(kit).setCurrentStreak(kitData.get(kit).getCurrentStreak() + 1);

        if (kitData.get(kit).getCurrentStreak() > kitData.get(kit).getBestStreak()) {
            setBestWinStreak(kit, kitData.get(kit).getCurrentStreak());
        }
    }

    public void setBestWinStreak(Kit kit, int value) {
        kitData.get(kit).setBestStreak(value);
    }

    public void addElo(Kit kit, int elo) {
        kitData.get(kit).setElo(kitData.get(kit).getElo() + elo);
    }
}
