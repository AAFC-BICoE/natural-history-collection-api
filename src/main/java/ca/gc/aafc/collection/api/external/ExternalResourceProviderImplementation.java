package ca.gc.aafc.collection.api.external;

import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ExternalResourceProviderImplementation implements ExternalResourceProvider {

  public static final Map<String, String> typeToReferenceMap = Map.of(
    "metadata", "objectstore/api/v1/metadata"
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
