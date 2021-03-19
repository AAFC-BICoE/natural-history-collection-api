package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.dina.dto.RelatedEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@RelatedEntity(ManagedAttribute.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "managed-attribute")
public class ManagedAttributeDto {

  @JsonApiId
  private UUID uuid;
  private String name;
  private String key;
  private ManagedAttribute.ManagedAttributeType managedAttributeType;
  private ManagedAttribute.ManagedAttributeComponent managedAttributeComponent;
  private String[] acceptedValues;
  private OffsetDateTime createdOn;
  private String createdBy;

}
