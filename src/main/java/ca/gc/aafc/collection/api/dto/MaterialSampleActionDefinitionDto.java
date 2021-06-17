package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import lombok.Data;

@Data
@RelatedEntity(MaterialSampleActionDefinition.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = MaterialSampleActionDefinitionDto.TYPENAME)
public class MaterialSampleActionDefinitionDto {

  public static final String TYPENAME = "material-sample-action-definition";
  
  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;

  private String createdBy;

  private String name;

  private String group;

  private MaterialSampleActionDefinition.ActionType actionType;

  @JsonApiField(patchStrategy = PatchStrategy.SET)
  private Map<MaterialSampleFormComponent, FormTemplate> formTemplates;
}
