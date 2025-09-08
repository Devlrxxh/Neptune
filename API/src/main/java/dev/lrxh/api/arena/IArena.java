package dev.lrxh.api.arena;

import java.util.concurrent.CompletableFuture;

public interface IArena {
    String getName();
    String getDisplayName();
    boolean isEnabled();
    boolean isSetup();
    CompletableFuture<? extends IArena> createDuplicate();
}
