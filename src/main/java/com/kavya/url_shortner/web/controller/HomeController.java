package com.kavya.url_shortner.web.controller;

import com.kavya.url_shortner.ApplicationProperties;
import com.kavya.url_shortner.domain.Exceptions.ShortUrlNotFoundException;
import com.kavya.url_shortner.domain.entities.ShortUrl;
import com.kavya.url_shortner.domain.entities.User;
import com.kavya.url_shortner.domain.model.CreateShortUrlCmd;
import com.kavya.url_shortner.domain.model.PagedResult;
import com.kavya.url_shortner.domain.model.shortUrlDto;
import com.kavya.url_shortner.domain.services.shortUrlService;
import com.kavya.url_shortner.web.dtos.CreateShortUrlForm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private ApplicationProperties properties;
    @Autowired
    public shortUrlService Shorturlservice;

    @Autowired
    public SecurityUtils securityUtils;

    private void addShortUrlsDataToModel(Model model, int pageNo) {
        PagedResult<shortUrlDto> shortUrls = Shorturlservice.findAllPublicShortUrls(pageNo, properties.pageSize());
        model.addAttribute("shortUrls", shortUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
    }
    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "1")Integer page,
            Model model
    ){
        User currentUser = securityUtils.getCurrentUser();
        this.addShortUrlsDataToModel(model, page);
        model.addAttribute("paginationUrl","/");
        model.addAttribute("createShortUrlForm",new CreateShortUrlForm("",false,null));
        return "index";
    }

    @PostMapping("/short-urls")
    String createShortUrl(@ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if(bindingResult.hasErrors()) {
            this.addShortUrlsDataToModel(model, 1);
            return "index";
        }
        try{
            Long userId=securityUtils.getCurrentUserId();
            CreateShortUrlCmd cmd=new CreateShortUrlCmd(form.originalUrl()
                    ,form.isPrivate(),form.expirationInDays(),userId);
            var shortUrlDto=Shorturlservice.createShortUrl(cmd);
            redirectAttributes.addFlashAttribute("successMessage", "Short URL created successfully "+
                    properties.baseUrl()+"/s/"+shortUrlDto.shortKey());
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage","Failed to create short url!");
        }

        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    String redirectToOriginalUrl(@PathVariable String shortKey) {
        Long userId=securityUtils.getCurrentUserId();
        Optional<shortUrlDto> shortUrlDtoOptional = Shorturlservice.accessShortUrl(shortKey,userId);
        if(shortUrlDtoOptional.isEmpty()) {
            throw new ShortUrlNotFoundException("Invalid short key: "+shortKey);
        }
        shortUrlDto shortUrlDto = shortUrlDtoOptional.get();
        return "redirect:"+shortUrlDto.originalUrl();
    }


    @GetMapping("/login")
    String loginForm() {
        return "login";
    }

    @GetMapping("/my-urls")
    public String showUserUrls(
            @RequestParam(defaultValue = "1") int page,
            Model model) {
        var currentUserId = securityUtils.getCurrentUserId();
        PagedResult<shortUrlDto> myUrls =
                Shorturlservice.getUserShortUrls(currentUserId, page, properties.pageSize());
        model.addAttribute("shortUrls", myUrls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("paginationUrl", "/my-urls");
        return "my-urls";
    }

    @PostMapping("/delete-urls")
    public String deleteUrls(
            @RequestParam(value = "ids", required = false) List<Long> ids,
            RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "No URLs selected for deletion");
            return "redirect:/my-urls";
        }
        try {
            var currentUserId = securityUtils.getCurrentUserId();
            Shorturlservice.deleteUserShortUrls(ids, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Selected URLs have been deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting URLs: " + e.getMessage());
        }
        return "redirect:/my-urls";
    }
}
