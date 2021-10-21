package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ca.gc.aafc.dina.entity.DinaEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class CollectionSequence implements DinaEntity {

  @Id
  @NonNull
  @Column(name = "collection_id")
  private Integer id;

  @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, optional = false)
  @PrimaryKeyJoinColumn(name = "collection_id", referencedColumnName = "id")
  private Collection collection;

  private Integer counter;

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
