package ca.gc.aafc.collection.api;

import io.crnk.spring.setup.boot.core.CrnkBootConfigurer;
import org.geolatte.geom.json.GeolatteGeomModule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.config.ResourceNameIdentifierConfig;
import ca.gc.aafc.dina.service.ResourceNameIdentifierService;

/**
 * Used to avoid circular references when loading configuration.
 */
@Configuration
@AutoConfigureBefore(ResourceNameIdentifierService.class)
public class AdditionalConfiguration {

  @Bean
  public ResourceNameIdentifierConfig provideResourceNameIdentifierConfig() {
    return ResourceNameIdentifierConfig.builder().
      config(MaterialSample.class, new ResourceNameIdentifierConfig.ResourceNameConfig("materialSampleName", "group"))
      .build();
  }

  @Bean
  public CrnkBootConfigurer bootConfigurer() {
    return boot -> boot.getObjectMapper().registerModule(new GeolatteGeomModule());
  }
}
