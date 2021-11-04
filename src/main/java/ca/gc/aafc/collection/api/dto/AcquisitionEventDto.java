package ca.gc.aafc.collection.api.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import ca.gc.aafc.collection.api.entities.AcquisitionEvent;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.repository.meta.JsonApiExternalRelation;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

@RelatedEntity(AcquisitionEvent.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = AcquisitionEventDto.TYPENAME)
@TypeName(AcquisitionEventDto.TYPENAME)
public class AcquisitionEventDto {

  public static final String TYPENAME = "acquisition-event";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  @JsonApiExternalRelation(type = "person")
  @JsonApiRelation
  private ExternalRelationDto receivedFrom;

  private LocalDate receivedDate;

  private String receptionRemarks;

  @JsonApiExternalRelation(type = "person")
  @JsonApiRelation
  private ExternalRelationDto externallyIsolatedBy;

  private LocalDate externallyIsolatedOn;

  private String externallyIsolationRemarks;
  
}
