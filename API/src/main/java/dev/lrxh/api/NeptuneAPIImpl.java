package dev.lrxh.api;

import dev.lrxh.api.kit.IKitService;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.profile.IProfileService;
import dev.lrxh.api.scoreboard.IScoreboardService;

public class NeptuneAPIImpl implements NeptuneAPI {

    private final IProfileService profileService;
    private final IMatchService matchService;
    private final IKitService kitService;
    private final IScoreboardService scoreboardService;

    public NeptuneAPIImpl(IProfileService profileService, IMatchService matchService, IKitService kitService, IScoreboardService scoreboardService) {
        this.profileService = profileService;
        this.matchService = matchService;
        this.kitService = kitService;
        this.scoreboardService = scoreboardService;
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
}
