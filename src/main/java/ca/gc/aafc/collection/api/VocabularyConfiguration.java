package ca.gc.aafc.collection.api;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;

@Configuration
@PropertySource(value = "classpath:vocabulary/doe.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/srs.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/coordinateSystem.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/typeStatus.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/substrate.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class VocabularyConfiguration {

  private final Map<String, List<VocabularyElement>> vocabulary;

  public VocabularyConfiguration(Map<String, List<VocabularyElement>> vocabulary) {
    this.vocabulary = vocabulary;
  }

  public Map<String, List<VocabularyElement>> getVocabulary() {
    return vocabulary;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class VocabularyElement {
    private String name;
    private String term;
    private Map<String, String> labels;
  }
}
