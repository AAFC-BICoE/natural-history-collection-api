package ca.gc.aafc.collection.api;

import java.util.List;

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
  
  private List<VocabularyDto> groups;

  public VocabularyConfiguration(
    List<VocabularyDto> groups
  ) {
    this.groups = groups;
  }
  
  public List<VocabularyDto> getGroups() {
    return groups;
  }

}
