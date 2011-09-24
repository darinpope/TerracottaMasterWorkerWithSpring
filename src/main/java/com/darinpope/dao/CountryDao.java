package com.darinpope.dao;

import com.darinpope.model.Country;

public interface CountryDao {
  Country getCountry(Integer countryId);
  void deleteCountry(Integer countryId);
  void clearCountryCache();
}
