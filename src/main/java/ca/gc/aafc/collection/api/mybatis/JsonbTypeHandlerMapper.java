package ca.gc.aafc.collection.api.mybatis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * package protected helper class that provides a single ObjectMapper instance
 * for all jsonb-based TypeHandler
 */
final class JsonbTypeHandlerMapper {

  private static final ObjectMapper OM = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private JsonbTypeHandlerMapper() {
    // utility class
  }

  /**
   * Read a value using the shared ObjectMapper.
   *
   * @param jsonStr
   * @param clazz
   * @return Instance of T with the content of jsonStr. Note that unknowns properties are ignored.
   * @throws JsonProcessingException a
   */
  static <T> T readValue(String jsonStr, Class<T> clazz) throws JsonProcessingException {
    return OM.readValue(jsonStr, clazz);
  }

  static String writeValue(Object obj) throws JsonProcessingException {
    return OM.writeValueAsString(obj);
  }

}