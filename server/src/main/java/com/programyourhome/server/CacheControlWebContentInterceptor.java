package com.programyourhome.server;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Component
public class CacheControlWebContentInterceptor extends WebContentInterceptor {

    @Value("${cache.image.seconds}")
    private int imageCacheInSeconds;

    @PostConstruct
    public void init() {
        this.setCacheMappings(this.createCacheMappings());
    }

    private Properties createCacheMappings() {
        final Properties cacheMappingProperties = new Properties();
        // Cache image files.
        final String imageCacheString = "" + this.imageCacheInSeconds;
        cacheMappingProperties.put("/**/*.png", imageCacheString);
        cacheMappingProperties.put("/**/*.jpg", imageCacheString);
        cacheMappingProperties.put("/**/*.jpeg", imageCacheString);
        cacheMappingProperties.put("/**/*.gif", imageCacheString);
        return cacheMappingProperties;
    }

}
