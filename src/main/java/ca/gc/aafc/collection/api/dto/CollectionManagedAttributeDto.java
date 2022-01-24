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

import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

@RelatedEntity(CollectionManagedAttribute.class)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Data
@JsonApiResource(type = CollectionManagedAttributeDto.TYPENAME)
@TypeName(CollectionManagedAttributeDto.TYPENAME)
public class CollectionManagedAttributeDto {

  public static final String TYPENAME = "managed-attribute";

  @JsonApiId
  @Id
  @PropertyName("id")
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
