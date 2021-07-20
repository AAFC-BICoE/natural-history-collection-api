package ca.gc.aafc.collection.api;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.collection.api.dto.VocabularyConfigurationDto;
import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;

@Configuration
@PropertySource(value = "classpath:vocabulary/doe.yml",
  factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class VocabularyConfiguration {
  
  private Map<String, VocabularyConfigurationDto> degreeOfEstablishment;

  public VocabularyConfiguration(
    Map<String, VocabularyConfigurationDto> degreeOfEstablishment
  ) {
    this.degreeOfEstablishment = degreeOfEstablishment;
  }
  
  public Map<String, VocabularyConfigurationDto> getDegreeOfEstablishment() {
    return degreeOfEstablishment;
  }

}
