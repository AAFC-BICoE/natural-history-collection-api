package ca.gc.aafc.collection.api;

import javax.inject.Inject;

import ca.gc.aafc.dina.search.messaging.producer.LogBasedMessageProducer;
import ca.gc.aafc.dina.search.messaging.producer.MessageProducer;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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

  /**
   * Provides a fallback MessageProducer when messaging.isProducer is false.
   */
  @Configuration
  public static class FallbackMessageProducer {
    @Bean
    @ConditionalOnProperty(name = "messaging.isProducer", havingValue = "false")
    public MessageProducer init() {
      return new LogBasedMessageProducer();
    }
  }

}
