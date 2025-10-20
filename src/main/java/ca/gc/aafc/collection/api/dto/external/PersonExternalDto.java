package ca.gc.aafc.collection.api.dto.external;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.dina.dto.JsonApiExternalResource;

/**
 * Represents an external relationship of type person.
 */
@Builder
@Getter
@JsonApiTypeForClass(PersonExternalDto.EXTERNAL_TYPENAME)
public class PersonExternalDto implements JsonApiExternalResource {

  public static final String EXTERNAL_TYPENAME = "person";

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
