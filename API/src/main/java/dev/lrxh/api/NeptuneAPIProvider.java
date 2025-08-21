package dev.lrxh.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class NeptuneAPIProvider {

    private static NeptuneAPI api;

    public static NeptuneAPI getAPI() {
        if (api == null) {
            setupAPI();
        }
        return api;
    }

    private static void setupAPI() {
        RegisteredServiceProvider<NeptuneAPI> provider =
                Bukkit.getServer().getServicesManager().getRegistration(NeptuneAPI.class);
        if (provider != null) {
            api = provider.getProvider();
        }
    }
}
