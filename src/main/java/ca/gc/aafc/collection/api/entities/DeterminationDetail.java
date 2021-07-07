package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class DeterminationDetail {
  private final String verbatimScientificName;
  private final String verbatimAgent;
  private final String verbatimDate;
}
