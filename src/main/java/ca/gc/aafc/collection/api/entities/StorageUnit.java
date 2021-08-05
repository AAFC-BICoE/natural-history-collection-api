package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class StorageUnit extends AbstractStorageUnit {


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_storage_unit_id")
  private StorageUnit parentStorageUnit;

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_storage_unit_id", referencedColumnName = "id")
  private List<ImmutableStorageUnit> storageUnitChildren = new ArrayList<>();

  @Transient
  private List<StorageHierarchicalObject> hierarchy;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "storage_unit_type_id")
  private StorageUnitType storageUnitType;

}
