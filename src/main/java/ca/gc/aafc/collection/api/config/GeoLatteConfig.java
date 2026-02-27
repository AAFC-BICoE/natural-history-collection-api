package ca.gc.aafc.collection.api.config;

import org.geolatte.geom.json.GeolatteGeomModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoLatteConfig {
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizer() {
    return builder -> builder.modules(new GeolatteGeomModule());
  }
}
