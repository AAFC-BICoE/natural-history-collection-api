package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;

/**
 * DTO representing a request to get the identifier that should follow the provided one.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiResource(type = MaterialSampleIdentifierGeneratorDto.TYPENAME)
public class MaterialSampleIdentifierGeneratorDto {

  public static final String TYPENAME = "material-sample-identifier-generator";

  /**
   * The id here is not a real id since this resource is only computing values and nothing is stored.
   *
   */
  @JsonApiId
  private String id;

  // for single parent use
  private UUID currentParentUUID;
  private Integer quantity;

  // for multiple parents use (only 1 generated identifier per parent supported)
  private List<UUID> currentParentsUUID;

  private MaterialSampleNameGeneration.IdentifierGenerationStrategy strategy;
  private MaterialSample.MaterialSampleType materialSampleType;
  private MaterialSampleNameGeneration.CharacterType characterType;

  /**
   * used to return the result
   */
  @JsonApiField(postable = false)
  private Map<UUID, List<String>> nextIdentifiers;

}
