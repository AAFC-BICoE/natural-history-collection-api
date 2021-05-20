package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionRun;
import ca.gc.aafc.dina.dto.RelatedEntity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import lombok.Data;

@Data
@RelatedEntity(MaterialSampleActionRun.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = MaterialSampleActionRunDto.TYPENAME)
public class MaterialSampleActionRunDto {

  public static final String TYPENAME = "material-sample-action-run";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private UUID agentId;

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  @JsonApiRelation
  private MaterialSampleActionDefinitionDto materialSampleActionDefinition;

  @JsonApiRelation
  private MaterialSampleDto sourceMaterialSample;
  
}
