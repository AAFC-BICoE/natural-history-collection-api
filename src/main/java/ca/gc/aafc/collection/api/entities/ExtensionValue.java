package ca.gc.aafc.collection.api.entities;

import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Value
public class ExtensionValue {
  
  private String extKey;
  private String extVersion;
  private String extTerm;
  private String value;
}
