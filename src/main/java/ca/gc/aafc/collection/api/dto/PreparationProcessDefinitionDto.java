package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.PreparationProcessDefinition;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import lombok.Data;

@Data
@RelatedEntity(PreparationProcessDefinition.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = PreparationProcessDefinitionDto.TYPENAME)
public class PreparationProcessDefinitionDto {

  public static final String TYPENAME = "preparation-process-definition";
  
  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private String group;
}
