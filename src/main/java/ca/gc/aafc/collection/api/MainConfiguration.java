package ca.gc.aafc.collection.api;

import ca.gc.aafc.dina.DinaBaseApiAutoConfiguration;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.CustomFieldResolverSpec;
import ca.gc.aafc.dina.mapper.JpaDtoMapper;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan(basePackageClasses = DinaBaseApiAutoConfiguration.class)
@ImportAutoConfiguration(DinaBaseApiAutoConfiguration.class)
public class MainConfiguration {

  /**
   * Configures DTO-to-Entity mappings.
   * 
   * @return the DtoJpaMapper
   */
  @Bean
  public JpaDtoMapper dtoJpaMapper(BaseDAO baseDAO) {
    Map<Class<?>, List<CustomFieldResolverSpec<?>>> customFieldResolvers = new HashMap<>();

    return new JpaDtoMapper(Collections.emptyMap(), customFieldResolvers);
  }

}
