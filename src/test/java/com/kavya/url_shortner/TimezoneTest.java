package com.kavya.url_shortner;

import org.junit.jupiter.api.Test;

public class TimezoneTest {

    @Test
    void printTimezone() {
        System.out.println("TZ=" + java.util.TimeZone.getDefault().getID());
        System.out.println("Zone=" + java.time.ZoneId.systemDefault());
    }
}