package ca.gc.aafc.collection.api;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import io.crnk.core.engine.parser.StringMapper;
import io.crnk.core.engine.registry.ResourceRegistry;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
public class MainConfiguration {

  @Inject
  @SuppressWarnings("deprecation")
  public void setupManagedAttributeLookup(ResourceRegistry resourceRegistry) {
    var resourceInfo = resourceRegistry.getEntry(ManagedAttributeDto.class)
      .getResourceInformation();

    var defaultIdStringMapper = resourceInfo.getIdStringMapper();
    resourceInfo.setIdStringMapper(new StringMapper<>(){
      @Override
      public Object parse(String input) {
        // If the input's not in UUID format then use the raw string as the ID:
        try {
          UUID.fromString(input);
        } catch (IllegalArgumentException e) {
          return input;
        }
        return defaultIdStringMapper.parse(input);
      }

      @Override
      public String toString(Object input) {
        return defaultIdStringMapper.toString(input);
      }
    });
  }

}
