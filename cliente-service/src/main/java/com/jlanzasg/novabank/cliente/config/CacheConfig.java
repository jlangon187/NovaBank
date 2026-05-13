package com.jlanzasg.novabank.cliente.config;

import org.springframework.boot.cache.autoconfigure.CacheManagerCustomizer;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Cache config.
 */
@Configuration
public class CacheConfig {

    /**
     * Cache manager customizer cache manager customizer.
     *
     * @return the cache manager customizer
     */
    @Bean
    public CacheManagerCustomizer<CaffeineCacheManager> cacheManagerCustomizer() {
        return cacheManager -> cacheManager.setAsyncCacheMode(true);
    }
}