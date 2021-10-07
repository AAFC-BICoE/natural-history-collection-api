package ca.gc.aafc.collection.api.external;

import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ExternalResourceProviderImplementation implements ExternalResourceProvider {

  public static final Map<String, String> TYPE_TO_REFERENCE_MAP = Map.of(
    "metadata", "objectstore/api/v1/metadata",
    "person", "agent/api/v1/person"
  );

  @Override
  public String getReferenceForType(String type) {
    return TYPE_TO_REFERENCE_MAP.get(type);
  }

  @Override
  public Set<String> getTypes() {
    return TYPE_TO_REFERENCE_MAP.keySet();
  }
}
