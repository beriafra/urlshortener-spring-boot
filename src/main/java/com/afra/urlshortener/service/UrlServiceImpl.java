package com.afra.urlshortener.service;

import com.afra.urlshortener.model.Url;
import com.afra.urlshortener.model.UrlDto;
import com.afra.urlshortener.repository.UrlRepository;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class UrlServiceImpl implements UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlServiceImpl.class);

    private final UrlRepository urlRepository;

    public UrlServiceImpl(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Override
    public Url generateShortLinkWithUser(UrlDto urlDto, String userName) {

        if(StringUtils.isNotEmpty(urlDto.getUrl()))
        {
            String encodedUrl = encodeUrl(urlDto.getUrl());
            Url urlToPersist = new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            urlToPersist.setUserId(userName);
            urlToPersist.setEnabled(true);
            Url urlToRet = persistShortLink(urlToPersist);

            if(urlToRet != null)
                return urlToRet;

            return null;
        }
        return null;
    }

    @Override
    public Url persistShortLink(Url url) {
        return urlRepository.save(url);
    }

    @Override
    public Url getEncodedUrl(String url) {
        return urlRepository.findByShortLink(url);
    }

    @Override
    public void deleteShortLink(Url url) {

        urlRepository.delete(url);
    }

    @Override
    public List<Url> getAllUrlsForUser(String userId) {
        return urlRepository.findByUserId(userId);
    }

    @Override
    public void enableUrl(String shortLink, String userId) {
        Url url = urlRepository.findByShortLink(shortLink);
        if (url != null && url.getUserId().equals(userId)) {
            url.setEnabled(true);
            urlRepository.save(url);
        } else {
            throw new RuntimeException("URL does not exist or you do not have permission to enable it");
        }
    }

    @Override
    public void disableUrl(String shortLink, String userId) {
        Url url = urlRepository.findByShortLink(shortLink);
        if (url != null && url.getUserId().equals(userId)) {
            url.setEnabled(false);
            urlRepository.save(url);
        } else {
            throw new RuntimeException("URL does not exist or you do not have permission to disable it");
        }
    }

    @Override
    public List<Url> getAllUrlsForUserWithStats(String userId) {
        return urlRepository.findByUserId(userId);
    }


    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate)
    {
        if(StringUtils.isBlank(expirationDate))
        {
            return creationDate.plusMinutes(10);
        }
        return LocalDateTime.parse(expirationDate);
    }

    private String encodeUrl(String url)
    {
        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.murmur3_32_fixed()
                .hashString(url.concat(time.toString()), StandardCharsets.UTF_8)
                .toString();
        return  encodedUrl;
    }

}