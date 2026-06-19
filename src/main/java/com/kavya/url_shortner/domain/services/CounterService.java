package com.kavya.url_shortner.domain.services;

import com.kavya.url_shortner.domain.entities.ShortUrlCounter;
import com.kavya.url_shortner.domain.repositories.ShortUrlCounterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CounterService {

    @Autowired
    private final StringRedisTemplate redisTemplate;
    private final ShortUrlCounterRepository repository;

    @Transactional
    public long nextId() {

        Long counter =
                redisTemplate.opsForValue()
                        .increment("shorturl:counter");

        Long rangeEnd =
                Long.valueOf(
                        redisTemplate.opsForValue()
                                .get("shorturl:rangeEnd"));

        if(counter >= rangeEnd) {

            ShortUrlCounter dbCounter =
                    repository.findById(1L)
                            .orElseThrow();

            long newRangeEnd =
                    dbCounter.getRangeEnd()
                            + dbCounter.getRangeSize();

            dbCounter.setRangeEnd(newRangeEnd);

            repository.save(dbCounter);

            redisTemplate.opsForValue()
                    .set(
                            "shorturl:rangeEnd",
                            String.valueOf(newRangeEnd)
                    );
        }

        return counter;
    }
}
