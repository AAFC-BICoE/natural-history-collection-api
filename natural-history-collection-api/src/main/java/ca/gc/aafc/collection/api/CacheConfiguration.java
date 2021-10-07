package ca.gc.aafc.collection.api;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Log4j2
public class CacheConfiguration {

  @Bean
  public Caffeine caffeineConfig() {
    return Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES);
  }

  @Bean
  public CacheManager cacheManager(Caffeine caffeine) {
    CaffeineCacheManager ccm = new CaffeineCacheManager();
    ccm.setCaffeine(caffeine);
    return ccm;
  }
}
