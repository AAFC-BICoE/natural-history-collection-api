package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ca.gc.aafc.collection.api.entities.Determination;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiResource(type = MaterialSampleSummaryDto.TYPENAME)
public class MaterialSampleSummaryDto {

  public static final String TYPENAME = "material-sample-summary";

  @JsonApiId
  private UUID uuid;

  private String materialSampleName;

  private List<Determination> effectiveDeterminations;

}
