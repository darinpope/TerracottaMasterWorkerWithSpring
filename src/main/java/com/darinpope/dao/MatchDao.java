package com.darinpope.dao;

import com.darinpope.model.Match;

public interface MatchDao {
    Match getMatch(Integer matchId);
    void deleteMatch(Integer matchId);
    void clearMatchCache();
}
