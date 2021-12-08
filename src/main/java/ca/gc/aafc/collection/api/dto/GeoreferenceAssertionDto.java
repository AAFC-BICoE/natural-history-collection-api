package ca.gc.aafc.collection.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Value
public class GeoreferenceAssertionDto {
  private Double dwcDecimalLatitude;
  private Double dwcDecimalLongitude;
  private Integer dwcCoordinateUncertaintyInMeters;
  private OffsetDateTime createdOn;
  private LocalDate dwcGeoreferencedDate;
  private List<UUID> georeferencedBy;
  private String literalGeoreferencedBy;
  private String dwcGeoreferenceProtocol;
  private String dwcGeoreferenceSources;
  @Size(max = 1000)
  private String dwcGeoreferenceRemarks;
  private String dwcGeodeticDatum;
  private Boolean isPrimary;
  private GeoreferenceVerificationStatus dwcGeoreferenceVerificationStatus;

  public enum GeoreferenceVerificationStatus {
    GEOREFERENCING_NOT_POSSIBLE
  }
}
