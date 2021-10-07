package ca.gc.aafc.collection.api.mixs;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FieldExtensionDefinition {

  private Extension extension;
 
  @Builder
  @Getter
  public static class Extension {
  
    private String name;
    private String key;
    private String version;
    private List<Field> fields;
  }
  
  @Builder
  @Getter
  public static class Field {
  
    private String term;
    private String name;
    private String definition;
    private String dinaComponent;
  }

}
