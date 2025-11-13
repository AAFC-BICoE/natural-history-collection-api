package ca.gc.aafc.collection.api.dto;

import java.util.List;
import java.util.UUID;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;

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
@JsonApiTypeForClass(MaterialSampleSummaryDto.TYPENAME)
public class MaterialSampleSummaryDto {

  public static final String TYPENAME = "material-sample-summary";

  @JsonApiId
  private UUID uuid;

  private String materialSampleName;

  private List<Determination> effectiveDeterminations;

}
