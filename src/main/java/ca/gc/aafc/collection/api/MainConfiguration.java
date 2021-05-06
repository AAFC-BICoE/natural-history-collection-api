package ca.gc.aafc.collection.api;

import javax.inject.Inject;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.util.ManagedAttributeIdMapper;
import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import io.crnk.core.engine.registry.ResourceRegistry;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
public class MainConfiguration {

  @Inject
  @SuppressWarnings({"deprecation", "unchecked"})
  public void setupManagedAttributeLookup(ResourceRegistry resourceRegistry) {
    var resourceInfo = resourceRegistry.getEntry(CollectionManagedAttributeDto.class)
      .getResourceInformation();

    resourceInfo.setIdStringMapper(
      new ManagedAttributeIdMapper(resourceInfo.getIdStringMapper()));
  }

}
