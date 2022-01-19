package ca.gc.aafc.collection.api.entities;

import javax.validation.constraints.Size;

import org.javers.core.metamodel.annotation.Value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@SuperBuilder
@Value // This class is considered a "value" belonging to a MaterialSample.
public class HostOrganism {
  
  @Size(max = 150)
  private String name;

  @Size(max = 1000)
  private String remarks;
}
