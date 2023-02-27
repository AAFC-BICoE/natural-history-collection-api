package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

  public enum IdentifierGenerationStrategy {TYPE_BASED, DIRECT_PARENT}
  public enum CharacterType {NUMBER, LOWER_LETTER, UPPER_LETTER}

  /**
   * The id here is not a real id since this resource is only computing values and nothing is stored.
   *
   */
  @JsonApiId
  private String id;

  private UUID currentParentUUID;
  private IdentifierGenerationStrategy strategy;
  private CharacterType characterType;

  private Integer amount;

  /**
   * used to return the result
   */
  @JsonApiField(postable = false)
  public List<String> nextIdentifiers;

}
