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

import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.dina.dto.RelatedEntity;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelatedEntity(StorageUnitCoordinates.class)
@JsonApiResource(type = StorageUnitCoordinatesDto.TYPENAME)
public class StorageUnitCoordinatesDto {

  public static final String TYPENAME = "storage-unit-coordinates";

  @JsonApiId
  private UUID uuid;

  private Integer wellColumn;
  private String wellRow;

  private OffsetDateTime createdOn;
  private String createdBy;

  @JsonApiRelation
  private StorageUnitDto storageUnit;

}
