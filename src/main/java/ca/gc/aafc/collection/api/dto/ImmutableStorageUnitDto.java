package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.crnk.core.resource.annotations.JsonApiId;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(ImmutableStorageUnit.class)
public class ImmutableStorageUnitDto {

  @JsonApiId
  @JsonIgnore
  private UUID uuid;

  @IgnoreDinaMapping
  private UUID id;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
    this.id = uuid;
  }

  public void setId(UUID id) {
    this.uuid = id;
    this.id = id;
  }
}
