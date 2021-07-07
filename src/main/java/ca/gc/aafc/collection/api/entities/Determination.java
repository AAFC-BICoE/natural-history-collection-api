package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class Determination {

  private final String verbatimScientificName;
  private final String verbatimAgent;
  private final String verbatimDate;

  private final List<DeterminationDetail> details;

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class DeterminationDetail {
    private final String typeStatus;
    private final String typeStatusEvidence;
    private final UUID determiner;
    private final LocalDate determinedOn;
    private final String qualifier;
    private final String scientificNameSource;
    private final String scientificNameDetails;
  }

}
