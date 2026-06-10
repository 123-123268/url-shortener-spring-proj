package com.kavya.url_shortner.domain.services;

import com.kavya.url_shortner.domain.entities.ShortUrl;
import com.kavya.url_shortner.domain.model.shortUrlDto;
import com.kavya.url_shortner.domain.repositories.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShortUrlCacheService {

    @Autowired
    private ShortUrlRepository repository;

    @Autowired
    private EntityMapper entityMapper;

    @Cacheable(value = "shortUrls", key = "#shortKey")
    public Optional<shortUrlDto> getShortUrl(String shortKey) {

        System.out.println("Fetching from DB");

        return repository.findByShortKey(shortKey)
                .map(entityMapper::toShortUrlDto);
    }

    @CacheEvict(value = "shortUrls", key = "#shortKey")
    public void evictShortUrl(String shortKey) {

        System.out.println("Cache removed for: " + shortKey);
    }
}