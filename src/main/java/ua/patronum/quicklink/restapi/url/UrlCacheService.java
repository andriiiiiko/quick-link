package ua.patronum.quicklink.restapi.url;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;
import ua.patronum.quicklink.data.entity.Url;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UrlCacheService {

    private static final String CACHE_NAME = "OriginalUrl";
    private static final String CACHE_ERROR = "CACHE_ERROR";

    @Getter
    private final ConcurrentMapCacheManager cacheManager;

    @Getter
    private String shortUrl;

    @Cacheable(value = CACHE_NAME, key = "#shortUrl")
    public Url getCachedUrl(String shortUrl) {
        Cache.ValueWrapper wrapper = Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).get(shortUrl);

        if (wrapper != null) {
            Url cachedUrl = (Url) wrapper.get();
            assert cachedUrl != null;
            Error cacheError = checkCacheError(cachedUrl.getOriginalUrl());

            if (cacheError != null) {
                return handleCacheError(cacheError);
            }

            return cachedUrl;
        }
        return null;
    }

    private Url handleCacheError(Error cacheError) {
        throw new RuntimeException("Cache error: " + cacheError);
    }

    @CacheEvict(value = CACHE_NAME, key = "#shortUrl")
    public void evictCache(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @Cacheable(value = CACHE_NAME, key = "#shortUrl", unless = "#result == null")
    public void cacheUrl(String shortUrl, Url url) {
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME)).put(shortUrl, url);
    }

    public Error checkCacheError(String errorMessage) {
        return CACHE_ERROR.equals(errorMessage) ? Error.CACHE_ERROR : null;
    }
}