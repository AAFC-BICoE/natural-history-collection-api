package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.dto.RelatedEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import lombok.Data;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@RelatedEntity(StorageUnit.class)
@JsonApiResource(type = StorageUnitDto.TYPENAME)
@TypeName(StorageUnitDto.TYPENAME)
public class StorageUnitDto {

  public static final String TYPENAME = "storage-unit";

  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  private String barcode;

  @ShallowReference
  @JsonApiRelation
  private StorageUnitDto parentStorageUnit;

  @ShallowReference
  @JsonApiRelation
  private StorageUnitTypeDto storageUnitType;

  @DiffIgnore
  private List<ImmutableStorageUnitDto> storageUnitChildren = List.of();

  @DiffIgnore
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<StorageHierarchicalObject> hierarchy;

}
