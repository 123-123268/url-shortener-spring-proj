package com.kavya.url_shortner.domain.Exceptions;


public class ShortUrlNotFoundException extends RuntimeException {
    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}
