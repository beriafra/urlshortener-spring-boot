package com.afra.urlshortener.repository;

import com.afra.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long>{
    public Url findByShortLink(String shortLink);
    public List<Url> findByUserId(String userId);
}
