package ca.gc.aafc.collection.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import ca.gc.aafc.dina.vocabulary.VocabularyConfiguration;

@Configuration
@PropertySource(value = "classpath:vocabulary/doe.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/srs.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/coordinateSystem.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/typeStatus.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/substrate.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/materialSampleState.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/associationType.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class CollectionVocabularyConfiguration extends VocabularyConfiguration {
  
  private final Map<String, List<CollectionVocabularyElement>> collectionVocabulary;
  
  public CollectionVocabularyConfiguration(Map<String, List<VocabularyElement>> vocabulary, Map<String, List<CollectionVocabularyElement>> collectionVocabulary) {
    super(vocabulary);
    this.collectionVocabulary = collectionVocabulary;
  }

  public Map<String, List<CollectionVocabularyElement>> getCollectionVocabulary() {
    return collectionVocabulary;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class CollectionVocabularyElement extends VocabularyElement {

    private String inverseOf;

  }
}
