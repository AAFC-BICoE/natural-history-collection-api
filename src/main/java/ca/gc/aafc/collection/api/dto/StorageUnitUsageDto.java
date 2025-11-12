package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toedter.spring.hateoas.jsonapi.JsonApiId;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.dto.RelatedEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(StorageUnitUsage.class)
@JsonApiTypeForClass(StorageUnitUsageDto.TYPENAME)
public class StorageUnitUsageDto implements JsonApiResource {

  public static final String TYPENAME = "storage-unit-usage";

  @JsonApiId
  private UUID uuid;

  private Integer wellColumn;
  private String wellRow;
  private String usageType;

  // read-only calculated fields
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer cellNumber;
  
  private String storageUnitName;

  private OffsetDateTime createdOn;
  private String createdBy;

  // -- Relationships --
  @JsonIgnore
  private StorageUnitTypeDto storageUnitType;

  @JsonIgnore
  private StorageUnitDto storageUnit;

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
