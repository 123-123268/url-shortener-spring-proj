package com.kavya.url_shortner.config;

import com.kavya.url_shortner.domain.entities.ShortUrlCounter;
import com.kavya.url_shortner.domain.repositories.ShortUrlCounterRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CounterInitializer {

    private final ShortUrlCounterRepository repository;
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    @Transactional
    public void initialize() {

        if(redisTemplate.hasKey("shorturl:counter")) {
            return;
        }

        ShortUrlCounter counter =
                repository.findById(1L)
                        .orElseThrow();

        long reserved = counter.getCounterValue() + 10;

        counter.setCounterValue(reserved);

        repository.save(counter);

        redisTemplate.opsForValue()
                .set("shorturl:counter",
                        String.valueOf(reserved));

        redisTemplate.opsForValue()
                .set("shorturl:rangeEnd",
                        String.valueOf(counter.getRangeEnd()));
    }
}
