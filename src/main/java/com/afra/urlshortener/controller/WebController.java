package com.afra.urlshortener.controller;

import com.afra.urlshortener.model.Url;
import com.afra.urlshortener.model.UrlDto;
import com.afra.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class WebController {
    private UrlService urlService;

    public WebController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        return "index";
    }

    @GetMapping("/shortenUrl")
    public String shortenUrl(Model model) {
        model.addAttribute("urlDto", new UrlDto());
        return "shortenUrl";
    }

    @PostMapping("/generateShortLink")
    public String generateShortLink(@ModelAttribute UrlDto urlDto, Model model, Authentication authentication) {
        Url urlToRet = urlService.generateShortLinkWithUser(urlDto, authentication.getName());
        model.addAttribute("originalUrl", urlToRet.getOriginalUrl());
        model.addAttribute("shortUrl", urlToRet.getShortLink());
        return "result";
    }

    @GetMapping("/urls")
    public ModelAndView getAllUrlsForCurrentUser(Authentication authentication) {
        String userId = authentication.getName(); // Get the authenticated user's ID
        List<Url> urls = urlService.getAllUrlsForUser(userId);
        ModelAndView modelAndView = new ModelAndView("urls"); // The name of your Thymeleaf template
        modelAndView.addObject("urls", urls);
        return modelAndView;
    }

    @PostMapping("/disable/{shortLink}")
    public String disableUrl(@PathVariable String shortLink, Authentication authentication) {
        String userId = authentication.getName(); // Get the authenticated user's ID
        urlService.disableUrl(shortLink, userId);
        return "redirect:/urls";
    }

    @PostMapping("/enable/{shortLink}")
    public String enableUrl(@PathVariable String shortLink, Authentication authentication) {
        String userId = authentication.getName(); // Get the authenticated user's ID
        urlService.enableUrl(shortLink, userId);
        return "redirect:/urls";
    }


}
