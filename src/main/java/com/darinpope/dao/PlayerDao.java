package com.darinpope.dao;

import com.darinpope.model.Player;

public interface PlayerDao {
    Player getPlayer(Integer playerId);
    void deletePlayer(Integer playerId);
    void clearPlayerCache();
}
