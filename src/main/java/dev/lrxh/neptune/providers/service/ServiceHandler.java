package dev.lrxh.neptune.providers.service;

import dev.lrxh.neptune.Neptune;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ServiceHandler {
    private final Logger logger = Neptune.get().getLogger();
    private boolean shouldContinue = true;

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }

    public <T> T startService(Supplier<T> supplier) {
        if (!shouldContinue) return null;

        T service;
        try {
            service = supplier.get();
        } catch (Exception e) {
            logger.severe("Error while loading service: " + e.getMessage());
            shouldContinue = false;
            return null;
        }

        return service;
    }
}
