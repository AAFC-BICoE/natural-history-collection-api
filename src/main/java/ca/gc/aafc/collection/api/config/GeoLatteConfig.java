package ca.gc.aafc.collection.api.config;

import org.geolatte.geom.json.jackson3.GeolatteGeomModule;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.DeserializationFeature;

@Configuration
public class GeoLatteConfig {
  @Bean
  public JsonMapperBuilderCustomizer customizer() {
    return builder -> {
      builder.addModule(new GeolatteGeomModule());
      builder.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    };
  }
}
