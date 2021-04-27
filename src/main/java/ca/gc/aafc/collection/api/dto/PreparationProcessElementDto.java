package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.PreparationProcessElement;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;

import java.time.OffsetTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import lombok.Data;

@Data
@RelatedEntity(PreparationProcessElement.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = PreparationProcessElementDto.TYPENAME)
public class PreparationProcessElementDto {

  public static final String TYPENAME = "preparation-process-element";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetTime createdOn;

  private String createdBy;

  @JsonApiRelation
  private PreparationProcessDto preparationProcess;

  @JsonApiRelation
  private MaterialSampleDto materialSample;

  
}
