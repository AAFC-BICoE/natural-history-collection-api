package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.MaterialSampleCounter;

/**
 * DTO representing a request to get or increment a counter of a material-sample.
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiResource(type = MaterialSampleCounterDto.TYPENAME)
public class MaterialSampleCounterDto {

  public static final String TYPENAME = "material-sample-counter";

  @JsonApiId
  private UUID uuid;

  private UUID materialSampleUUID;

  private String counterName;
  private Integer amount;

  @JsonApiField(postable = false)
  private MaterialSampleCounter.IncrementFunctionOutput result;

}
