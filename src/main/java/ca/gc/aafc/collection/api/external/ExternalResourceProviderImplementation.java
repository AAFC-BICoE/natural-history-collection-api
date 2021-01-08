package ca.gc.aafc.collection.api.external;

import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ExternalResourceProviderImplementation implements ExternalResourceProvider {

  public static final Map<String, String> typeToReferenceMap = Map.of(
    "file", "objectstore/api/v1/file",
    "agent", "agent/api/v1/person"
  );

  @Override
  public String getReferenceForType(String type) {
    return typeToReferenceMap.get(type);
  }

  @Override
  public Set<String> getTypes() {
    return typeToReferenceMap.keySet();
  }
}