package com.kavya.url_shortner.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UrlExistenceValidator {
    private static final Logger log = LoggerFactory.getLogger(UrlExistenceValidator.class);

    public static boolean isUrlExists(String urlString) {
        try {
            URI uri = new URI(urlString);

            return uri.getScheme() != null &&
                    (uri.getScheme().equals("http")
                            || uri.getScheme().equals("https"));

        } catch (Exception e) {
            return false;
        }
    }
}
