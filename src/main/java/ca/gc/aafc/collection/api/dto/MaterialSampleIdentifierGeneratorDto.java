package ca.gc.aafc.collection.api.dto;

import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.jsonapi.JsonApiImmutable;

/**
 * DTO representing a request to get the identifier that should follow the provided one.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass(MaterialSampleIdentifierGeneratorDto.TYPENAME)
public class MaterialSampleIdentifierGeneratorDto implements JsonApiResource {

  public static final String TYPENAME = "material-sample-identifier-generator";

  /**
   * The id here is not a real id since this resource is only computing values and nothing is stored.
   *
   */
  @JsonApiId
  private UUID id;

  // for single parent use
  private UUID currentParentUUID;
  private Integer quantity;

  // for multiple parents use (only 1 generated identifier per parent supported)
  private List<UUID> currentParentsUUID;

  private MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy;
  private MaterialSample.MaterialSampleType materialSampleType;
  private MaterialSampleNameGeneration.CharacterType characterType;
  private SplitConfiguration.Separator separator;

  /**
   * used to return the result
   */
  @JsonApiImmutable(JsonApiImmutable.ImmutableOn.CREATE)
  private Map<UUID, List<String>> nextIdentifiers;

  @Override
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  public UUID getJsonApiId() {
    return id;
  }
}
