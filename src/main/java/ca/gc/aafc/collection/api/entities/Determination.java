package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class Determination {

  @Size(max = 250)
  private final String verbatimScientificName;

  @Size(max = 150)
  private final String verbatimAgent;

  @Size(max = 50)
  private final String verbatimDate;

  private final List<DeterminationDetail> details;

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class DeterminationDetail {

    @Size(max = 50)
    private final String typeStatus;

    @Size(max = 250)
    private final String typeStatusEvidence;

    private final List<UUID> determiner;

    private final LocalDate determinedOn;

    @Size(max = 150)
    private final String qualifier;

    @Size(max = 50)
    private final String scientificNameSource;

    @Size(max = 250)
    private final String scientificNameDetails;
  }

}
