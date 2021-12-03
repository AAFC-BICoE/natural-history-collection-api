package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@RelatedEntity(CollectionManagedAttribute.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = "managed-attribute")
public class CollectionManagedAttributeDto {

  @JsonApiId
  private UUID uuid;
  private String name;
  private String key;
  private CollectionManagedAttribute.ManagedAttributeType managedAttributeType;
  private CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent;
  private String[] acceptedValues;
  private OffsetDateTime createdOn;
  private String createdBy;
  private String group;
  private MultilingualDescription multilingualDescription;

}
