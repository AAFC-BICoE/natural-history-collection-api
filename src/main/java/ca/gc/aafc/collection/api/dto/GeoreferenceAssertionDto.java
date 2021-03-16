package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RelatedEntity(GeoreferenceAssertion.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = GeoreferenceAssertionDto.TYPENAME)
@TypeName(GeoreferenceAssertionDto.TYPENAME)
public class GeoreferenceAssertionDto {

  public static final String TYPENAME = "georeference-assertion";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String createdBy;
  private OffsetDateTime createdOn;

  private Double dwcDecimalLatitude;
  private Double dwcDecimalLongitude;
  private Integer dwcCoordinateUncertaintyInMeters;
  private LocalDate dwcGeoreferencedDate;
  private String literalGeoreferencedBy;
  private String dwcGeoreferenceProtocol;
  private String dwcGeoreferenceSources;
  private String dwcGeoreferenceRemarks;
  private String dwcGeodeticDatum;

  @JsonApiExternalRelation(type = "agent")
  @JsonApiRelation
  private List<ExternalRelationDto> georeferencedBy;

  @JsonApiRelation
  private CollectingEventDto collectingEvent;  

}
