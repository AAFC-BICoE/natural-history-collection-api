package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.util.Map;

public class ExtensionValueTestFixture {

  public static final String EXTENSION_KEY = "mixs_soil_v5";
  public static final String EXTENSION_FIELD_KEY = "experimental_factor";
  public static final String EXTENSION_VALUE = "the value";

  public static Map<String, Map<String, String>> newExtensionValue() {
    return newExtensionValue(EXTENSION_KEY, EXTENSION_FIELD_KEY, EXTENSION_VALUE);
  }

  public static Map<String, Map<String, String>> newExtensionValue(String key, String fieldKey, String value) {
    return Map.of(key, Map.of(fieldKey, value));
  }
}
