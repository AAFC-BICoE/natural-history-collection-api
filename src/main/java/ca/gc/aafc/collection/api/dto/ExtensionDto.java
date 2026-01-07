package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@AllArgsConstructor
@Getter
@JsonApiTypeForClass(ExtensionDto.TYPENAME)
public class ExtensionDto {

  public static final String TYPENAME = "extension";

  @JsonApiId
  private final String id;

  private final Extension extension;
  
}
