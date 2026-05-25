package com.kavya.url_shortner.domain.model;

public record CreateShortUrlCmd(
        String originalUrl,
        Boolean isPrivate,
        Integer expirationInDays,
        Long userId
) {
}