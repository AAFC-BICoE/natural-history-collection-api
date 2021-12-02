package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonApiResource(type = "extension")
public class ExtensionDto {

  @JsonApiId
  private final String id;

  private final Extension extension;
  
}
