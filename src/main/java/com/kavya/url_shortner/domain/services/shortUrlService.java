package com.kavya.url_shortner.domain.services;

import com.kavya.url_shortner.ApplicationProperties;
import com.kavya.url_shortner.domain.entities.ShortUrl;
import com.kavya.url_shortner.domain.model.CreateShortUrlCmd;
import com.kavya.url_shortner.domain.model.PagedResult;
import com.kavya.url_shortner.domain.model.shortUrlDto;
import com.kavya.url_shortner.domain.repositories.ShortUrlRepository;
import com.kavya.url_shortner.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional(readOnly = true)
public class shortUrlService {
    @Autowired
    private ApplicationProperties properties;

    @Autowired
    public ShortUrlRepository shortUrlRepository;
    @Autowired
    public EntityMapper entityMapper;

    @Autowired
    public UserRepository userRepository;

    public PagedResult<shortUrlDto> findAllPublicShortUrls(int pageNumber, int pageSize) {
        pageNumber=pageNumber>1?pageNumber-1:0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<shortUrlDto> shortUrlDtoPage = shortUrlRepository.findPublicShortUrls(pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlDtoPage);
    }
    @Transactional
    public shortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        if(properties.validateOriginalUrl()) {
            boolean urlExists = UrlExistenceValidator.isUrlExists(cmd.originalUrl());
            if(!urlExists) {
                throw new RuntimeException("Invalid URL "+cmd.originalUrl());
            }
        }
        var shortKey = generateUniqueShortKey();
        var shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setShortKey(shortKey);
        if(cmd.userId() == null) {
            shortUrl.setCreatedBy(null);
            shortUrl.setIsPrivate(false);
            shortUrl.setExpiresAt(Instant.now().plus(properties.defaultExpiryInDays(), DAYS));
        } else {
            shortUrl.setCreatedBy(userRepository.findById(cmd.userId()).orElseThrow());
            shortUrl.setIsPrivate(cmd.isPrivate() != null && cmd.isPrivate());
            shortUrl.setExpiresAt(cmd.expirationInDays() != null ? Instant.now().plus(cmd.expirationInDays(), DAYS) : null);
        }
        shortUrl.setClickCount(0L);
        shortUrl.setCreatedAt(Instant.now());
        shortUrlRepository.save(shortUrl);
        return entityMapper.toShortUrlDto(shortUrl);
    }
    private String generateUniqueShortKey() {
        String shortKey;
        do {
            shortKey = generateRandomShortKey();
        } while (shortUrlRepository.existsByShortKey(shortKey));
        return shortKey;
    }
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomShortKey() {
        StringBuilder sb = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Transactional
    public Optional<shortUrlDto> accessShortUrl(String shortKey,Long userId) {
        Optional<ShortUrl> shortUrlOptional = shortUrlRepository.findByShortKey(shortKey);
        if(shortUrlOptional.isEmpty()) {
            return Optional.empty();
        }
        ShortUrl shortUrl = shortUrlOptional.get();
        if(shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        if(shortUrl.getIsPrivate() != null && shortUrl.getCreatedBy() != null
                && !Objects.equals(shortUrl.getCreatedBy().getId(), userId)) {
            return Optional.empty();
        }
        shortUrl.setClickCount(shortUrl.getClickCount()+1);
        shortUrlRepository.save(shortUrl);
        return shortUrlOptional.map(entityMapper::toShortUrlDto);
    }

    public PagedResult<shortUrlDto> getUserShortUrls(Long userId, int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage = shortUrlRepository.findByCreatedById(userId, pageable)
                .map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }
    private Pageable getPageable(int page, int size) {
        page = page > 1 ? page - 1: 0;
        return PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
    }
    @Transactional
    public void deleteUserShortUrls(List<Long> ids, Long userId) {
        if (ids != null && !ids.isEmpty() && userId != null) {
            shortUrlRepository.deleteByIdInAndCreatedById(ids, userId);
        }
    }
    public PagedResult<shortUrlDto> findAllShortUrls(int page, int pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        var shortUrlsPage =  shortUrlRepository.findAllShortUrls(pageable).map(entityMapper::toShortUrlDto);
        return PagedResult.from(shortUrlsPage);
    }
}
