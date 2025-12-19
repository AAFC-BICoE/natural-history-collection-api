package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Data
@Builder
@RequiredArgsConstructor
@Value
// This class is considered a "value" belonging to a MaterialSample:
@org.javers.core.metamodel.annotation.Value
public class ScheduledActionDto {

  @Size(max = 25)
  private String actionType;

  private LocalDate date;

  @Size(max = 120)
  private String actionStatus;

  @JsonApiExternalRelation(type = "user")
  private ExternalRelationDto assignedTo;

  @Size(max = 1000)
  private String remarks;
}
