package ca.gc.aafc.collection.api;

import ca.gc.aafc.dina.property.YamlPropertyLoaderFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource(value = "classpath:LocationConfiguration.yml",
  factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties
public class LocationConfiguration {

  private final List<String> coordinateSystem;
  private final List<String> srs;

  public LocationConfiguration(List<String> coordinateSystem, List<String> srs) {
    this.coordinateSystem = coordinateSystem;
    this.srs = srs;
  }

  public List<String> getCoordinateSystem() {
    return coordinateSystem;
  }

  public List<String> getSrs() {
    return srs;
  }
}
