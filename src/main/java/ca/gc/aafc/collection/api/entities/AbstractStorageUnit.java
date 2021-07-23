package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import ca.gc.aafc.dina.entity.DinaEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@AllArgsConstructor
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
public class AbstractStorageUnit implements DinaEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NaturalId
  @NotNull
  @Column(unique = true)
  private UUID uuid;

  @NotBlank
  @Size(max = 150)
  private String name;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @Column(name = "created_on", insertable = false, updatable = false)
  @Generated(value = GenerationTime.INSERT)
  private OffsetDateTime createdOn;

  @NotBlank
  @Column(name = "created_by", updatable = false)
  private String createdBy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_storage_unit_id")
  private StorageUnit parentStorageUnit;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentStorageUnit")
  @ToString.Exclude
  private List<ImmutableStorageUnitChild> storageUnitChildren = new ArrayList<>();

  @Transient
  private List<StorageHierarchicalObject> hierarchy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_type_id")
  private StorageUnitType storageUnitType;

}
