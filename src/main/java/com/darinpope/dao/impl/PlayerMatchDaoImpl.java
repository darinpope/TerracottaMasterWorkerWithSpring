package com.darinpope.dao.impl;

import com.darinpope.dao.MatchDao;
import com.darinpope.dao.PlayerDao;
import com.darinpope.dao.PlayerMatchDao;
import com.darinpope.model.Country;
import com.darinpope.model.Match;
import com.darinpope.model.Player;
import com.darinpope.model.PlayerMatch;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository("playerMatchDao")
public class PlayerMatchDaoImpl extends SimpleJdbcDaoSupport implements PlayerMatchDao {

    @Resource
    private Ehcache playerMatchCache;

    @Resource
    private PlayerDao playerDao;

    @Resource
    private MatchDao matchDao;

    public List<Player> getPlayerByMatchId(Integer pMatchId) {
        Attribute<Integer> matchId = playerMatchCache.getSearchAttribute("matchId");
        Results results = playerMatchCache.createQuery().includeValues().addCriteria(matchId.eq(pMatchId)).execute();
        List<Player> players = new ArrayList<Player>();
        for(Result result:results.all()) {
            PlayerMatch playerMatch = (PlayerMatch) result.getValue();
            players.add(playerDao.getPlayer(playerMatch.getPlayerId()));
        }
        return players;
    }

    public List<Match> getMatchByPlayerId(Integer pPlayerId) {
        Attribute<Integer> playerId = playerMatchCache.getSearchAttribute("playerId");
        Results results = playerMatchCache.createQuery().includeValues().addCriteria(playerId.eq(pPlayerId)).execute();
        List<Match> matches = new ArrayList<Match>();
        for(Result result:results.all()) {
            PlayerMatch playerMatch = (PlayerMatch) result.getValue();
            matches.add(matchDao.getMatch(playerMatch.getMatchId()));
        }
        return matches;
    }

    public void loadPlayerMatchCache() {
        playerMatchCache.setNodeBulkLoadEnabled(true);
        String sql="select player_id,match_id from playermatch";
        List<PlayerMatch> playerMatches = this.getSimpleJdbcTemplate().query(sql,new PlayerMatchMapper(),new HashMap<String,String>());
        for(PlayerMatch playerMatch:playerMatches) {
            playerMatchCache.put(new Element(playerMatch.getPlayerId()+"|"+playerMatch.getMatchId(),playerMatch));
        }
        playerMatchCache.setNodeBulkLoadEnabled(false);
    }

    private static final class PlayerMatchMapper implements RowMapper<PlayerMatch> {
      public PlayerMatch mapRow(ResultSet rs, int rowNum) throws SQLException {
        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setMatchId(rs.getInt("match_id"));
        playerMatch.setPlayerId(rs.getInt("player_id"));
        return playerMatch;
      }
    }

}
