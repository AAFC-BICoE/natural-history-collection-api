package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.CollectingEventManagedAttribute;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(CollectingEventManagedAttribute.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@JsonApiResource(type = "event-managed-attribute")
public class CollectingEventManagedAttributeDto {

  @JsonApiId
  private UUID uuid;

  @JsonApiRelation
  private CollectingEventDto event;

  @JsonApiRelation
  private ManagedAttributeDto attribute;

  private String assignedValue;

}
