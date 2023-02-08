package ca.gc.aafc.collection.api.entities;

import org.javers.core.metamodel.annotation.Value;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Value
public class ExtensionValue {

  // used to report error on validation
  public static final String FIELD_KEY_NAME = "extFieldKey";
  
  private String extKey;
  private String extFieldKey;
  private String value;

}
