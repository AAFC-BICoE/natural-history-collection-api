package ca.gc.aafc.collection.api.entities;

import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class Organism {
  
  @Size(max = 50)
  private final String lifeStage;

  @Size(max = 25)
  private final String sex;

  @Size(max = 50)
  private final String substrate;

  @Size(max = 1000)
  private final String remarks;
}
