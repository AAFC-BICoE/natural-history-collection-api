package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.dto.RelatedEntity;

import io.crnk.core.resource.annotations.JsonApiField;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import io.crnk.core.resource.annotations.PatchStrategy;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(Organism.class)
@JsonApiResource(type = OrganismDto.TYPENAME)
@TypeName(OrganismDto.TYPENAME)
public class OrganismDto {

  public static final String TYPENAME = "organism";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String group;

  private Boolean isTarget;

  private String lifeStage;
  private String sex;
  private String remarks;

  /**
   * Map of Managed attribute key to value object.
   */
  @JsonApiField(patchStrategy = PatchStrategy.SET)
  @Builder.Default
  private Map<String, String> managedAttributes = Map.of();

  private List<Determination> determination;

  private OffsetDateTime createdOn;
  private String createdBy;

}
