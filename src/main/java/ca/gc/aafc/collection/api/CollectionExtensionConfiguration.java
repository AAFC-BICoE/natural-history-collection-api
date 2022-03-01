package ca.gc.aafc.collection.api;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;

@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:extension/mixsSoil.yml", factory  = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:extension/mixsSediment.yml", factory  = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:extension/cfia_ppc.yml", factory  = YamlPropertyLoaderFactory.class)
public class CollectionExtensionConfiguration {

  private final Map<String, Extension> extension;
  
  public CollectionExtensionConfiguration(Map<String, Extension> extension) {
    this.extension = extension;
  }

  public Map<String, Extension> getExtension() {
    return extension;
  }

}
