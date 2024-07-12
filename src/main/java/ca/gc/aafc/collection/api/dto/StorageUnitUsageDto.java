package ca.gc.aafc.collection.api.dto;

import io.crnk.core.resource.annotations.JsonApiId;
import io.crnk.core.resource.annotations.JsonApiRelation;
import io.crnk.core.resource.annotations.JsonApiResource;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.dina.dto.RelatedEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(StorageUnitUsage.class)
@JsonApiResource(type = StorageUnitUsageDto.TYPENAME)
public class StorageUnitUsageDto {

  public static final String TYPENAME = "storage-unit-usage";

  @JsonApiId
  private UUID uuid;

  private Integer wellColumn;
  private String wellRow;
  private Integer cellNumber;

  private OffsetDateTime createdOn;
  private String createdBy;

  @JsonApiRelation
  private StorageUnitTypeDto storageUnitType;

  @JsonApiRelation
  private StorageUnitDto storageUnit;

}
