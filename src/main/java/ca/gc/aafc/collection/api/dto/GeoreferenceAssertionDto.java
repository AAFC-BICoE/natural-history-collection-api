package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.dina.dto.RelatedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(GeoreferenceAssertion.class)
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
  private String dwcGeoreferenceRemarks;
  private String dwcGeodeticDatum;
  private Boolean isPrimary;
  private GeoreferenceAssertion.GeoreferenceVerificationStatus dwcGeoreferenceVerificationStatus;
}
