package com.darinpope.dao.impl;

import com.darinpope.dao.CountryDao;
import com.darinpope.model.Country;
import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository("countryDao")
public class CountryDaoImpl extends SimpleJdbcDaoSupport implements CountryDao {

  @Cacheable(cacheName="CountryCache",
         keyGenerator = @KeyGenerator(
                name = "HashCodeCacheKeyGenerator",
                properties = @Property( name="includeMethod", value="false" )
         )
  )
  public Country getCountry(Integer countryId) {
    String sql = "select id,code,country_name,continent from country where id = ?";
    return this.getSimpleJdbcTemplate().queryForObject(sql, new CountryMapper(), countryId);
  }

  @TriggersRemove(cacheName="CountryCache",
         keyGenerator = @KeyGenerator(
                name = "HashCodeCacheKeyGenerator",
                properties = @Property( name="includeMethod", value="false" )
         )
  )
  public void deleteCountry(Integer countryId) {}

  @TriggersRemove(cacheName="CountryCache", removeAll=true)
  public void clearCountryCache() {}


  private static final class CountryMapper implements RowMapper<Country> {
    public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
      Country country = new Country();
      country.setCountryId(rs.getInt("id"));
      country.setCountryCode(rs.getString("code"));
      country.setName(rs.getString("country_name"));
      country.setContinent(rs.getString("continent"));
      return country;
    }
  }
}
