package com.darinpope.dao.impl;

import com.darinpope.dao.PlayerDao;
import com.darinpope.model.Country;
import com.darinpope.model.Player;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import net.sf.ehcache.Ehcache;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("playerDao")
public class PlayerDaoImpl extends SimpleJdbcDaoSupport implements PlayerDao {

    @Resource
    private Ehcache playerCache;

    @Cacheable(cacheName="PlayerCache",
           keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = @Property( name="includeMethod", value="false" )
           )
    )
    public Player getPlayer(Integer playerId) {
        String sql = "select id,first_name,last_name from player where id = ?";
        return this.getSimpleJdbcTemplate().queryForObject(sql, new PlayerMapper(), playerId);
    }

    @Cacheable(cacheName="PlayerCache",
           keyGenerator = @KeyGenerator(
                  name = "HashCodeCacheKeyGenerator",
                  properties = @Property( name="includeMethod", value="false" )
           )
    )
    public void deletePlayer(Integer playerId) {}

    @TriggersRemove(cacheName="PlayerCache", removeAll=true)
    public void clearPlayerCache() {}

    private static final class PlayerMapper implements RowMapper<Player> {
      public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
        Player player = new Player();
        player.setPlayerId(rs.getInt("id"));
        player.setFirstName(rs.getString("first_name"));
        player.setLastName(rs.getString("last_name"));
        return player;
      }
    }

}
