package com.kavya.url_shortner.domain.model;

public record CreateUserCmd(
        String email,
        String password,
        String name,
        Role role) {
}
