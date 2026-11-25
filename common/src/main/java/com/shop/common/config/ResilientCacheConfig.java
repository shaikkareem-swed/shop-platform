package com.shop.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

/** If Redis is down, log and fall through to the database instead of failing the request. */
@Configuration
public class ResilientCacheConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ResilientCacheConfig.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override public void handleCacheGetError(RuntimeException e, Cache c, Object k) { warn("get", c, e); }
            @Override public void handleCachePutError(RuntimeException e, Cache c, Object k, Object v) { warn("put", c, e); }
            @Override public void handleCacheEvictError(RuntimeException e, Cache c, Object k) { warn("evict", c, e); }
            @Override public void handleCacheClearError(RuntimeException e, Cache c) { warn("clear", c, e); }

            private void warn(String op, Cache c, RuntimeException e) {
                log.warn("Cache {} failed on '{}' (continuing without cache): {}", op, c.getName(), e.getMessage());
            }
        };
    }
}
