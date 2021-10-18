package ca.gc.aafc.collection.api;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;

@Configuration
@ConfigurationProperties
@PropertySource(value = "classpath:extension/mixsSoil.yml", factory  = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:extension/mixsSediment.yml", factory  = YamlPropertyLoaderFactory.class)
public class CollectionExtensionConfiguration {

  private final List<Extension> extension;
  
  public CollectionExtensionConfiguration(List<Extension> extension) {
    this.extension = extension;
  }

  public List<Extension> getExtension() {
    return extension;
  }

}
