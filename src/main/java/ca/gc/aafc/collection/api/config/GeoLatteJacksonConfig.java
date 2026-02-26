package ca.gc.aafc.collection.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.geolatte.geom.json.GeolatteGeomModule;

@Configuration
public class GeoLatteJacksonConfig {

  @Bean
  public GeolatteGeomModule geolatteGeomModule() {
    return new GeolatteGeomModule();
  }
}