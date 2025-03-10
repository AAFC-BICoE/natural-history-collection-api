package ca.gc.aafc.collection.api.config;

import java.util.List;
import java.util.Map;

import ca.gc.aafc.dina.vocabulary.VocabularyElementConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

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
@PropertySource(value = "classpath:vocabulary/materialSampleType.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/unitsOfMeasurement.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/protocolVocabulary.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/taxonomicRank.yml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:vocabulary/projectRole.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
@Validated
public class CollectionVocabularyConfiguration extends VocabularyConfiguration<CollectionVocabularyConfiguration.CollectionVocabularyElement> {

  public static final String PROTOCOL_DATA_VOCAB_KEY = "protocolData";
  public static final String PROTOCOL_TYPE_VOCAB_KEY = "protocolType";
  public static final String TAXONOMIC_RANK_KEY = "taxonomicRank";
  public static final String PROJECT_ROLE_VOCAB_KEY = "projectRole";

  public CollectionVocabularyConfiguration(Map<String, List<CollectionVocabularyElement>> vocabulary) {
    super(vocabulary);
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class CollectionVocabularyElement extends VocabularyElementConfiguration {
    private String inverseOf;
  }

  public List<CollectionVocabularyElement> getVocabularyByKey(String key) {
    return getVocabulary().get(key);
  }

}
