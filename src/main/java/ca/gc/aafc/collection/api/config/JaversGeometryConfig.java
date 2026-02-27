package ca.gc.aafc.collection.api.config;

import javax.inject.Inject;

import org.geolatte.geom.Geometry;
import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Allows Javers to serialize/deserialize Geometry as geojson.
 *
 */
@Configuration
public class JaversGeometryConfig {

  @Inject
  private ObjectMapper objectMapper;

  @Bean
  JsonTypeAdapter<Geometry<?>> geometryJsonTypeAdapter() {

    return new BasicStringTypeAdapter<>() {
      @Override
      public String serialize(Geometry sourceValue) {
        if (sourceValue == null) {
          return null;
        }
        try {
          return objectMapper.writeValueAsString(sourceValue);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Error serializing Geometry", e);
        }
      }

      @Override
      public Geometry<?> deserialize(String serializedValue) {
        if (serializedValue == null || serializedValue.isEmpty()) {
          return null;
        }
        try {
          return objectMapper.readValue(serializedValue, Geometry.class);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Error deserializing Geometry", e);
        }
      }

      @Override
      public Class<?> getValueType() {
        return Geometry.class;
      }
    };
  }
}
