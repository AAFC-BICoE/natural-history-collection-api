package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;

import javax.validation.constraints.Size;

import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import io.crnk.core.resource.annotations.JsonApiRelation;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Data
@Builder
@RequiredArgsConstructor
@Value
public class ScheduledActionDto {

  @Size(max = 25)
  private String actionType;

  private LocalDate date;

  @Size(max = 120)
  private String actionStatus;

  @JsonApiExternalRelation(type = "user")
  @JsonApiRelation
  private ExternalRelationDto assignedTo;

  @Size(max = 250)
  private String remarks;}
