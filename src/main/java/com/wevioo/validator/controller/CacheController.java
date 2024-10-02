package com.wevioo.validator.controller;

import com.wevioo.validator.service.CachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CacheController {

    @Autowired
    private CachingService cachingService;

    @GetMapping("/cached/countries")
    public List<String> getCachedCountries() {
        return cachingService.getCountries();
    }

    @GetMapping("/cached/currencies")
    public List<String> getCachedCurrencies() {
        return cachingService.getCurrencies();
    }
}
