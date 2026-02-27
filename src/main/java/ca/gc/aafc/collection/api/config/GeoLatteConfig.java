package ca.gc.aafc.collection.api.config;

import org.geolatte.geom.json.GeolatteGeomModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoLatteConfig {
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer customizer() {
    // make sure to add the module and not replace the existing list
    return builder -> builder.modulesToInstall(c -> c.add(new GeolatteGeomModule()));
  }
}
