package ca.gc.aafc.collection.api.entities;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class HostOrganism {
  
  @Size(max = 150)
  private String name;

  @Size(max = 250)
  private String remarks;
}
