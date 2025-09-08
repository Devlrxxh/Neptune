package dev.lrxh.api;

import dev.lrxh.api.kit.IKitService;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.api.queue.IQueueService;
import dev.lrxh.api.scoreboard.IScoreboardService;

public interface NeptuneAPI {
    IProfileService getProfileService();
    IMatchService getMatchService();
    IKitService getKitService();
    IScoreboardService getScoreboardService();
    IQueueService getQueueService();
}