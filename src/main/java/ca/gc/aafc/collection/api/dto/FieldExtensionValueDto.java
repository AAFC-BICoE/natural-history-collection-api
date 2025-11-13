package ca.gc.aafc.collection.api.dto;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonApiTypeForClass(FieldExtensionValueDto.TYPENAME)
public class FieldExtensionValueDto {

  public static final String TYPENAME = "field-extension-value";

  @JsonApiId
  private final String id;

  private final String extensionName;
  private final String extensionKey;
  private final Field field;

}
