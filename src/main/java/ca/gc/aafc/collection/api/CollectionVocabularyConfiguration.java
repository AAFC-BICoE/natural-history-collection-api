package ca.gc.aafc.collection.api;

import java.util.ArrayList;
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

  public CollectionVocabularyConfiguration(Map<String, List<CollectionVocabularyElement>> vocabulary) {
    super(temporaryCopy(vocabulary));
  }

  // to be fixed by #24967 Allow better extension of VocabularyConfiguration
  private static Map<String, List<VocabularyElement>> temporaryCopy(Map<String, List<CollectionVocabularyElement>> vocabulary) {
    Map<String, List<VocabularyElement>> newMap = new HashMap<>(vocabulary.size());
    for (Map.Entry<String, List<CollectionVocabularyElement>> entry : vocabulary.entrySet()) {
      newMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }
    return newMap;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class CollectionVocabularyElement extends VocabularyElement {
    private String inverseOf;
  }
}
