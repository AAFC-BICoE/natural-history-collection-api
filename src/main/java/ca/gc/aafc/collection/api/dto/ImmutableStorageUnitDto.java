package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.dina.dto.RelatedEntity;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(ImmutableStorageUnit.class)
public class ImmutableStorageUnitDto {
  
  private UUID uuid;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;
}
