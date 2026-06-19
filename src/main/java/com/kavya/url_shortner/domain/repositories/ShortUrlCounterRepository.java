package com.kavya.url_shortner.domain.repositories;

import com.kavya.url_shortner.domain.entities.ShortUrlCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortUrlCounterRepository
        extends JpaRepository<ShortUrlCounter, Long> {
}
