package dev.lrxh.api;

import dev.lrxh.api.kit.IKitService;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.api.queue.IQueueService;
import dev.lrxh.api.scoreboard.IScoreboardService;

public class NeptuneAPIImpl implements NeptuneAPI {

    private final IProfileService profileService;
    private final IMatchService matchService;
    private final IKitService kitService;
    private final IScoreboardService scoreboardService;
    private final IQueueService queueService;

    public NeptuneAPIImpl(IProfileService profileService, IMatchService matchService, IKitService kitService, IScoreboardService scoreboardService, IQueueService queueService) {
        this.profileService = profileService;
        this.matchService = matchService;
        this.kitService = kitService;
        this.scoreboardService = scoreboardService;
        this.queueService = queueService;
    }

    @Override
    public IProfileService getProfileService() {
        return profileService;
    }

    @Override
    public IMatchService getMatchService() {
        return matchService;
    }

    @Override
    public IKitService getKitService() {
        return kitService;
    }

    @Override
    public IScoreboardService getScoreboardService() {
        return scoreboardService;
    }
    
    @Override
    public IQueueService getQueueService() {
        return queueService;
    }
}
