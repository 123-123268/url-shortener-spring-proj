package com.kavya.url_shortner.domain.model;

import java.io.Serializable;

/**
 * DTO for {@link com.kavya.url_shortner.domain.entities.User}
 */
public record userDto(Long id, String name) implements Serializable {
}
