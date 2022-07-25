package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(StorageUnitType.class)
@JsonApiResource(type = StorageUnitTypeDto.TYPENAME)
@TypeName(StorageUnitTypeDto.TYPENAME)
public class StorageUnitTypeDto {

  public static final String TYPENAME = "storage-unit-type";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;
  private String group;
  private String name;
  private Boolean isInseperable;
  private StorageGridLayout gridLayoutDefinition;
}
