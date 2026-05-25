package com.kavya.url_shortner.domain.services;

import com.kavya.url_shortner.domain.entities.ShortUrl;
import com.kavya.url_shortner.domain.entities.User;
import com.kavya.url_shortner.domain.model.shortUrlDto;
import com.kavya.url_shortner.domain.model.userDto;
import org.springframework.stereotype.Component;


@Component
public class EntityMapper {
    public shortUrlDto toShortUrlDto(ShortUrl shortUrl) {
        userDto UserDto = null;
        if(shortUrl.getCreatedBy() != null) {
            UserDto = toUserDto(shortUrl.getCreatedBy());
        }

        return new shortUrlDto(
                shortUrl.getId(),
                shortUrl.getShortKey(),
                shortUrl.getOriginalUrl(),
                shortUrl.getIsPrivate(),
                shortUrl.getExpiresAt(),
                UserDto,
                shortUrl.getClickCount(),
                shortUrl.getCreatedAt()
        );
    }

    public userDto toUserDto(User user) {
        return new userDto(user.getId(), user.getName());
    }
}
