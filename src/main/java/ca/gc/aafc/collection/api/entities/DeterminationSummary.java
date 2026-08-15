package ca.gc.aafc.collection.api.entities;

import java.io.Serializable;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeterminationSummary implements Serializable {
  private Map<String, String> classification;
  private String typeStatus;
  private Map<String, String> managedAttributes;
}
