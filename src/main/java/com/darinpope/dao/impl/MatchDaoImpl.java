package com.darinpope.dao.impl;

import com.darinpope.dao.MatchDao;
import com.darinpope.model.Match;
import com.darinpope.model.Player;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.ehcache.Ehcache;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("matchDao")
public class MatchDaoImpl extends SimpleJdbcDaoSupport implements MatchDao {
    private static final Logger LOGGER = Logger.getLogger(MatchDaoImpl.class);
    @Resource
    private Ehcache matchCache;

    @Cacheable(cacheName="MatchCache",
           keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = @Property( name="includeMethod", value="false" )
           )
    )
    public Match getMatch(Integer matchId) {
        LOGGER.debug("matchId = " + matchId);
        String sql = "select id,field_name,start_time,end_time from match where id = ?";
        return this.getSimpleJdbcTemplate().queryForObject(sql, new MatchMapper(), matchId);
    }

    @Cacheable(cacheName="MatchCache",
           keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = @Property( name="includeMethod", value="false" )
           )
    )
    public void deleteMatch(Integer matchId) {}

    @TriggersRemove(cacheName="MatchCache", removeAll=true)
    public void clearMatchCache() {}

    private static final class MatchMapper implements RowMapper<Match> {
      public Match mapRow(ResultSet rs, int rowNum) throws SQLException {
        Match match = new Match();
        match.setMatchId(rs.getInt("id"));
        match.setFieldName(rs.getString("field_name"));
        match.setStartTime(rs.getDate("start_time"));
        match.setEndTime(rs.getDate("end_time"));
        return match;
      }
    }

}
