package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.dto.JsonApiCalculatedAttribute;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

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
@JsonApiTypeForClass(StorageUnitDto.TYPENAME)
@TypeName(StorageUnitDto.TYPENAME)
public class StorageUnitDto implements JsonApiResource {

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

  private Boolean isGeneric;

  @DiffIgnore
  private List<ImmutableStorageUnitDto> storageUnitChildren = List.of();

  @JsonApiCalculatedAttribute
  @DiffIgnore
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<StorageHierarchicalObject> hierarchy;

  // -- Relationships --
  @ShallowReference
  @JsonIgnore
  private StorageUnitDto parentStorageUnit;

  @ShallowReference
  @JsonIgnore
  private StorageUnitTypeDto storageUnitType;


  @Override
  @JsonIgnore
  public String getJsonApiType() {
    return TYPENAME;
  }

  @Override
  @JsonIgnore
  public UUID getJsonApiId() {
    return uuid;
  }

}
