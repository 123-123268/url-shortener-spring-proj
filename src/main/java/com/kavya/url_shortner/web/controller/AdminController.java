package com.kavya.url_shortner.web.controller;

import com.kavya.url_shortner.ApplicationProperties;
import com.kavya.url_shortner.domain.model.PagedResult;
import com.kavya.url_shortner.domain.model.shortUrlDto;
import com.kavya.url_shortner.domain.services.shortUrlService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final shortUrlService ShortUrlService;
    private final ApplicationProperties properties;

    public AdminController(shortUrlService ShortUrlService, ApplicationProperties properties) {
        this.ShortUrlService = ShortUrlService;
        this.properties = properties;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        PagedResult<shortUrlDto> allUrls = ShortUrlService.findAllShortUrls(page, properties.pageSize());
        model.addAttribute("shortUrls", allUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/admin/dashboard");
        return "admin-dashboard";
    }
}
