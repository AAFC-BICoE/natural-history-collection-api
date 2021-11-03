package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;
import ca.gc.aafc.dina.entity.DinaEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionSequenceGenerationRequest implements DinaEntity {

  @NotNull
  private UUID collectionId;

  @NotNull
  @Min(value = 0)
  @Builder.Default
  private Integer amount = 1;

  private CollectionSequenceReserved result;

  // Group is retrieved from the Collection.
  @NotBlank
  private String group;

  @Override
  public Integer getId() {
    return null;
  }

  @Override
  public UUID getUuid() {
    return collectionId;
  }

  @Override
  public String getCreatedBy() {
    return null;
  }

  @Override
  public OffsetDateTime getCreatedOn() {
    return null;
  }
}
