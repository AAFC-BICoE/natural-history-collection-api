package ca.gc.aafc.collection.api.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.dina.dto.JsonApiExternalResource;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents an external relationship of type person.
 */
@Builder
@Getter
@JsonApiTypeForClass(MetadataExternalDto.EXTERNAL_TYPENAME)
public class MetadataExternalDto implements JsonApiExternalResource {

  public static final String EXTERNAL_TYPENAME = "metadata";

  @JsonApiId
  private UUID uuid;

  @JsonIgnore
  @Override
  public String getJsonApiType() {
    return EXTERNAL_TYPENAME;
  }

  @JsonIgnore
  @Override
  public UUID getJsonApiId() {
    return uuid;
  }
}
