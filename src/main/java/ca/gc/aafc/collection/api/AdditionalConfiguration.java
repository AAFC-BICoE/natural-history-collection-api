package ca.gc.aafc.collection.api;

import io.crnk.spring.setup.boot.core.CrnkBootConfigurer;
import org.geolatte.geom.json.GeolatteGeomModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Used to avoid circular references when loading configuration.
 */
@Configuration
public class AdditionalConfiguration {

  @Bean
  public CrnkBootConfigurer bootConfigurer() {
    return boot -> boot.getObjectMapper().registerModule(new GeolatteGeomModule());
  }
}
