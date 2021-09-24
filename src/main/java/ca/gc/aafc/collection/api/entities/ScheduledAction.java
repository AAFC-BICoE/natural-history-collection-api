package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ScheduledAction {
  
  @Size(max = 25)
  private final String actionType;

  private final LocalDate date;

  @Size(max = 120)
  private final String actionStatus;

  private final UUID assignedTo;

  @Size(max = 250)
  private final String remarks;
}
