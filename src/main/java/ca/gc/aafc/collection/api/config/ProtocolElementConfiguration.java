package ca.gc.aafc.collection.api.config;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

/**
 * Protocol elements are more specialized than vocabulary.
 * They have composed element pointing to vocabulary (unitsOfMeasurement).
 */
@Configuration
@PropertySource(value = "classpath:protocol-element/protocolElements.yml", factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class ProtocolElementConfiguration {

  private final List<ProtocolElement> protocolElements;

  public ProtocolElementConfiguration(List<ProtocolElement> protocolElements) {
    this.protocolElements = protocolElements;
  }

  public List<ProtocolElement> getProtocolElements() {
    return protocolElements;
  }

  public record ProtocolElement(String name, Map<String, String> labels, List<ProtocolElementData> data) {
  }

  public record ProtocolElementData(String key, String unitsOfMeasurement) {
  }

}
