package ca.gc.aafc.collection.api.dto;

import org.javers.core.metamodel.annotation.Value;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Value // This class is considered a "value" belonging to a MaterialSample.
public class AssociationDto {

  private UUID associatedSample;
  private String associationType;
  private String remarks;
}
