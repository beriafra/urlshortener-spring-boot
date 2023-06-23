package com.afra.urlshortener.service;

import com.afra.urlshortener.model.Url;
import com.afra.urlshortener.model.UrlDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UrlService {
    public Url generateShortLinkWithUser(UrlDto urlDto, String userName);
//    public Url generateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public List<Url> getAllUrlsForUser(String userId);
    public  void  deleteShortLink(Url url);
    public void enableUrl(String shortLink, String userId);
    public void disableUrl(String shortLink, String userId);
    List<Url> getAllUrlsForUserWithStats(String userId);

}
