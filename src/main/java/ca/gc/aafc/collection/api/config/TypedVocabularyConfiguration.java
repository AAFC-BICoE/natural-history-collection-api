package ca.gc.aafc.collection.api.config;

import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * Configuration class responsible to load all files (specified by PropertySource annotation) from typed-vocabulary.
 * The top level of each file should be "typedVocabulary".
 */
@Configuration
@PropertySource(value = "classpath:typed-vocabulary/protocolElement.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class TypedVocabularyConfiguration {

  public static final String PROTOCOL_DATA_ELEMENT_KEY = "protocolDataElement";

  private final Map<String, List<TypedVocabularyElementImpl>> typedVocabulary;

  public TypedVocabularyConfiguration(Map<String, List<TypedVocabularyElementImpl>> typedVocabulary) {
    this.typedVocabulary = typedVocabulary;
  }

  public Map<String, List<TypedVocabularyElementImpl>> getTypedVocabulary() {
    return typedVocabulary;
  }

  public List<? extends TypedVocabularyElement> getProtocolDataElement() {
    return typedVocabulary.get(PROTOCOL_DATA_ELEMENT_KEY);
  }

  @NoArgsConstructor
  @Getter
  @Setter
  static class TypedVocabularyElementImpl implements TypedVocabularyElement {
    private String key;
    private String name;
    private VocabularyElementType vocabularyElementType;
    private String[] acceptedValues;
    private MultilingualTitle multilingualTitle;
    private MultilingualDescription multilingualDescription;
    private String term;
    private String unit;
  }
}
