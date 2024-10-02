package com.wevioo.validator.service;

import com.wevioo.validator.client.CountryClient;
import com.wevioo.validator.client.CurrencyClient;
import jakarta.persistence.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CachingService {

    @Autowired
    private CountryClient countryClient;

    @Autowired
    private CurrencyClient currencyClient;


    @Autowired
    private RedisTemplate<String, List<String>> redisTemplate;

    private static final String COUNTRY_CACHE_KEY = "countries";
    private static final String CURRENCY_CACHE_KEY = "currencies";

    public List<String> getCountries() {
        List<String> countries = redisTemplate.opsForValue().get(COUNTRY_CACHE_KEY);
        if (countries == null) {
            countries = countryClient.getAllCountries();
            redisTemplate.opsForValue().set(COUNTRY_CACHE_KEY, countries);
        }
        return countries;
    }

    public List<String> getCurrencies() {
        List<String> currencies = redisTemplate.opsForValue().get(CURRENCY_CACHE_KEY);
        if (currencies == null) {
            currencies = currencyClient.getAllCurrencies();
            redisTemplate.opsForValue().set(CURRENCY_CACHE_KEY, currencies);
        }
        return currencies;
    }

}