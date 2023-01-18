package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO representing a request to get the identifier that should follow the provided one.
 *
 */
@Data
@Builder
@JsonApiResource(type = MaterialSampleIdentifierGeneratorDto.TYPENAME)
public class MaterialSampleIdentifierGeneratorDto {

  public static final String TYPENAME = "material-sample-identifier-generator";

  @JsonApiId
  private String submittedIdentifier;
  
  private Integer amount;

  /**
   * used to return the result
   */
  @JsonApiField(postable = false)
  public List<String> nextIdentifiers;

}
