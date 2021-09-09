package ca.gc.aafc.collection.api.dto;

import ca.gc.aafc.collection.api.entities.ImmutableStorageUnit;
import ca.gc.aafc.dina.dto.RelatedEntity;
import ca.gc.aafc.dina.mapper.IgnoreDinaMapping;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.crnk.core.resource.annotations.JsonApiId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@RelatedEntity(ImmutableStorageUnit.class)
public class ImmutableStorageUnitDto {

  @JsonApiId
  private UUID uuid;

  @Setter(AccessLevel.NONE)
  @IgnoreDinaMapping
  private UUID id;

  private OffsetDateTime createdOn;
  private String createdBy;

  private String group;

  private String name;

  @JsonIgnore // Ignore Deserialization of field
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
    this.id = uuid;
  }

  @JsonProperty("uuid") // Keep serialization of field for crnk
  public UUID getUuid() {
    return uuid;
  }
}
