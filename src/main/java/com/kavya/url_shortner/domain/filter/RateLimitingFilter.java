package com.kavya.url_shortner.domain.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;



@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final int MAX_REQUESTS_CREATE = 3;
    private static final int MAX_REQUESTS_LOGIN=2;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
//        System.out.println(
//                request.getMethod() + " " + request.getRequestURI()
//        );

        String ip = request.getRemoteAddr();

        String path = request.getRequestURI();
        String key = "";
        int maxRequests = 0;

        if(path.startsWith("/short-urls") && request.getMethod().equals("POST")) {
            key = "rate_limit:create:" + ip;
            maxRequests = MAX_REQUESTS_CREATE;
        }
        else if(path.startsWith("/login") && request.getMethod().equals("POST")) {
            key = "rate_limit:login:" + ip;
            maxRequests = MAX_REQUESTS_LOGIN;
        }
        else {
            filterChain.doFilter(request, response);
            return;
        }

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(60));
        }

        if (count != null && count > maxRequests) {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }

//        System.out.println("Path: " + path);
//        System.out.println("Key: " + key);
//        System.out.println("Count: " + count);

        filterChain.doFilter(request, response);
        return ;
    }
}
