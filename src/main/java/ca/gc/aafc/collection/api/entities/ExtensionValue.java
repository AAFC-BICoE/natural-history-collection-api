package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
@Builder
@Value
public class ExtensionValue {
  
  private String extKey;
  private String extVersion;
  private String extTerm;
  private String value;
}
