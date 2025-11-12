package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import lombok.Data;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@RelatedEntity(StorageUnitType.class)
@JsonApiTypeForClass(StorageUnitTypeDto.TYPENAME)
@TypeName(StorageUnitTypeDto.TYPENAME)
public class StorageUnitTypeDto implements JsonApiResource {

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
