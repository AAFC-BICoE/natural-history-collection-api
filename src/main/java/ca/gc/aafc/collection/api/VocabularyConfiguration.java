package ca.gc.aafc.collection.api;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.collection.api.dto.VocabularyDto;
import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;

@Configuration
@PropertySource(value = "classpath:vocabulary/doe.yml",
  factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class VocabularyConfiguration {
  
  private Map<String, VocabularyDto> degreeOfEstablishment;

  public VocabularyConfiguration(
    Map<String, VocabularyDto> degreeOfEstablishment
  ) {
    this.degreeOfEstablishment = degreeOfEstablishment;
  }
  
  public Map<String, VocabularyDto> getDegreeOfEstablishment() {
    return degreeOfEstablishment;
  }

}
