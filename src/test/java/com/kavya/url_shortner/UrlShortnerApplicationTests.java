package com.kavya.url_shortner;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UrlShortnerApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void printTimezone() {
		System.out.println("TZ=" + java.util.TimeZone.getDefault().getID());
		System.out.println("Zone=" + java.time.ZoneId.systemDefault());
	}
}
