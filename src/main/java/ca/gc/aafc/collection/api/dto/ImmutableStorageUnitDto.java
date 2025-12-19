package ca.gc.aafc.collection.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

/**
 * We are not setting @RelatedEntity(ImmutableStorageUnit.class) on purpose
 * The mapping is done by the Mapper
 */
@Data
public class ImmutableStorageUnitDto {

  private UUID id;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

}
