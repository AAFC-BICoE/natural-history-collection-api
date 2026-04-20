package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

import ca.gc.aafc.dina.entity.DinaEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@NoArgsConstructor
public class CollectionSequence implements DinaEntity {

  @Id
  @Column(name = "collection_id")
  private Integer id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  private Collection collection;

  @NotNull
  @Builder.Default
  private Integer counter = 0;

  @Override
  public String getCreatedBy() {
    return null;
  }

  @Override
  public OffsetDateTime getCreatedOn() {
    return null;
  }

  @Override
  public UUID getUuid() {
    return null;
  }

}
