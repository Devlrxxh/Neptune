package dev.lrxh.neptune.feature.party.impl.advertise;

import java.util.HashMap;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.impl.AdvertiseRunnable;

public class AdvertiseService {
    private HashMap<Party, AdvertiseRunnable> advertisedParties = new HashMap<>();
    private static AdvertiseService instance;

    public static AdvertiseService get() {
        if (instance == null) instance = new AdvertiseService();

        return instance;
    }

    public void add(Party party) {
        AdvertiseRunnable runnable = new AdvertiseRunnable(party);
        runnable.start(SettingsLocale.PARTY_ADVERTISE_TIME.getInt() / 2, SettingsLocale.PARTY_ADVERTISE_TIME.getInt());
        advertisedParties.put(party, runnable);
    }
    public void remove(Party party) {
        AdvertiseRunnable runnable = advertisedParties.remove(party);
        if (runnable != null) runnable.stop();
    }

    public boolean has(Party party) {
        return advertisedParties.containsKey(party);
    }
    public boolean has(AdvertiseRunnable runnable) {
        return advertisedParties.containsValue(runnable);
    }
}
