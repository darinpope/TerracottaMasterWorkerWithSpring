package com.darinpope.dao;

import com.darinpope.model.Match;
import com.darinpope.model.Player;

import java.util.List;

public interface PlayerMatchDao {
    public List<Player> getPlayerByMatchId(Integer matchId);
    public List<Match> getMatchByPlayerId(Integer playerId);
    public void loadPlayerMatchCache();
}
