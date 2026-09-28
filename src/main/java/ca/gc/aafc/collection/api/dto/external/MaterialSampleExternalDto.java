package ca.gc.aafc.collection.api.dto.external;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.dina.dto.JsonApiExternalResource;
import lombok.Builder;
import lombok.Getter;

/**
 * Represents an external relationship of type material sample.
 * This should only be used by transaction until a migration is applied to change that into a real relationship.
 */
@Builder
@Getter
@JsonApiTypeForClass(MaterialSampleExternalDto.EXTERNAL_TYPENAME)
public class MaterialSampleExternalDto implements JsonApiExternalResource {

  public static final String EXTERNAL_TYPENAME = "external-material-sample";

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
