package ca.gc.aafc.collection.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

/**
 * Represents a resource-name-identifier query.
 *
 * To be used with jsonapi hateoas, not Crnk.
 */
@Data
@Builder
@JsonApiTypeForClass(ResourceNameIdentifierResponseDto.TYPE)
public class ResourceNameIdentifierResponseDto {

  public static final String TYPE = "resource-name-identifier";

  @JsonApiId
  private UUID id;
  private String name;

}
