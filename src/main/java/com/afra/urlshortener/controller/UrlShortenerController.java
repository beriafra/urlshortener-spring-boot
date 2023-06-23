package com.afra.urlshortener.controller;

import com.afra.urlshortener.model.Url;
import com.afra.urlshortener.model.UrlDto;
import com.afra.urlshortener.model.UrlErrorResponseDto;
import com.afra.urlshortener.model.UrlResponseDto;
import com.afra.urlshortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UrlShortenerController {
    private final UrlService urlService;

    public UrlShortenerController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto, Authentication authentication) {
        Url urlToRet = urlService.generateShortLinkWithUser(urlDto, authentication.getName());

        if(urlToRet != null) {
            UrlResponseDto urlResponseDto = new UrlResponseDto();
            urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
            urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
            urlResponseDto.setShortLink(urlToRet.getShortLink());
            return new ResponseEntity<>(urlResponseDto, HttpStatus.OK);
        }

        UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
        urlErrorResponseDto.setStatus("500");
        urlErrorResponseDto.setError("There was an error processing your request. please try again.");
        return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(shortLink)) {
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Invalid Url");
            urlErrorResponseDto.setStatus("400");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.BAD_REQUEST);
        }

        Url encodedUrl = urlService.getEncodedUrl(shortLink);

        if(!encodedUrl.getEnabled()){
            return new ResponseEntity<>("This url has been disabled by owner!", HttpStatus.FORBIDDEN);
        }

        if(encodedUrl.getExpirationDate().isBefore(LocalDateTime.now())) {
            urlService.deleteShortLink(encodedUrl);
            UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
            urlErrorResponseDto.setError("Url Expired. Please try generating a fresh one.");
            urlErrorResponseDto.setStatus("410");
            return new ResponseEntity<>(urlErrorResponseDto, HttpStatus.GONE);
        }

        // Update click count and last accessed timestamp
        encodedUrl.setClickCount(encodedUrl.getClickCount() + 1);
        encodedUrl.setLastAccessed(LocalDateTime.now());
        urlService.persistShortLink(encodedUrl);

        response.sendRedirect(encodedUrl.getOriginalUrl());
        return null;
    }

    @GetMapping("/user/urls")
    public ResponseEntity<?> getAllUrlsForUser(Authentication authentication) {
        List<Url> urls = urlService.getAllUrlsForUser(authentication.getName());
        return new ResponseEntity<>(urls, HttpStatus.OK);
    }

    @PutMapping("/enable/{shortLink}")
    public ResponseEntity<?> enableUrl(@PathVariable String shortLink, Authentication authentication) {
        try {
            urlService.enableUrl(shortLink, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/disable/{shortLink}")
    public ResponseEntity<?> disableUrl(@PathVariable String shortLink, Authentication authentication) {
        try {
            urlService.disableUrl(shortLink, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
