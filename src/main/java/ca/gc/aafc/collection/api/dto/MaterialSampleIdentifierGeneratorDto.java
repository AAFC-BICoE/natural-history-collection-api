package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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

  @JsonApiId
  private String id;

  private String submittedIdentifier;

  private Integer amount;

  /**
   * used to return the result
   */
  @JsonApiField(postable = false)
  public List<String> nextIdentifiers;

}
