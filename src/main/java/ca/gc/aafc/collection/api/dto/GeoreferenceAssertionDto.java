package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GeoreferenceAssertionDto {
  private Double dwcDecimalLatitude;
  private Double dwcDecimalLongitude;
  private Integer dwcCoordinateUncertaintyInMeters;
  private OffsetDateTime createdOn;
  private String createdBy;
  private LocalDate dwcGeoreferencedDate;
  private List<UUID> georeferencedBy;
  private String literalGeoreferencedBy;
  private String dwcGeoreferenceProtocol;
  private String dwcGeoreferenceSources;
  private String dwcGeoreferenceRemarks;
  private String dwcGeodeticDatum;
  private GeoreferenceAssertion.GeoreferenceVerificationStatus dwcGeoreferenceVerificationStatus;
}
