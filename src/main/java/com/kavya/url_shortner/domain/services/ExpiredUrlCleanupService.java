package com.kavya.url_shortner.domain.services;

import com.kavya.url_shortner.domain.repositories.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ExpiredUrlCleanupService {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupExpiredUrls() {

        System.out.println("Running expired URL cleanup");

        shortUrlRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
