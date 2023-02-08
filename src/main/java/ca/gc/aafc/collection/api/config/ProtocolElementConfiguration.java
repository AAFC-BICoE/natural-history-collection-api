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

/**
 * Protocol elements are typed vocabulary implementation.
 */
@Configuration
@PropertySource(value = "classpath:typed-vocabulary/protocolElement.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class ProtocolElementConfiguration {

  private final List<ProtocolElement> protocolDataElement;

  public ProtocolElementConfiguration(List<ProtocolElement> protocolDataElement) {
    this.protocolDataElement = protocolDataElement;
  }

  public List<ProtocolElement> getProtocolElements() {
    return protocolDataElement;
  }

  @NoArgsConstructor
  @Getter
  @Setter
  public static class ProtocolElement implements TypedVocabularyElement {
    private String key;
    private String name;
    private VocabularyElementType vocabularyElementType;
    private String[] acceptedValues;
    private MultilingualTitle multilingualTitle;
    private MultilingualDescription multilingualDescription;
    private String term;

  }
}
