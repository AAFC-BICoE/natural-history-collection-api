package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.dina.entity.DinaEntityIdentifiableByName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NaturalIdCache
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractStorageUnit implements DinaEntityIdentifiableByName {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  private String name;

  @Size(max = 50)
  private String barcode;

  @Column(name = "_group")
  private String group;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @Column(name = "created_by")
  private String createdBy;

}
