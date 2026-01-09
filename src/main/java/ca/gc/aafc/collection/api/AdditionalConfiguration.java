package ca.gc.aafc.collection.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.config.ResourceNameIdentifierConfig;

/**
 * Used to avoid circular references when loading configuration.
 */
@Configuration
public class AdditionalConfiguration {

  @Bean
  public ResourceNameIdentifierConfig provideResourceNameIdentifierConfig() {
    return ResourceNameIdentifierConfig.builder().
      config(MaterialSample.class, new ResourceNameIdentifierConfig.ResourceNameConfig("materialSampleName", "group"))
      .build();
  }
}
