package dev.lrxh.api.scoreboard;

import dev.lrxh.api.profile.IProfile;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IScoreboardService {

    void registerScoreboard(String state, Function<IProfile, List<String>> scoreboardFunction);
    List<String> getScoreboardLines(String state, IProfile profile);}
