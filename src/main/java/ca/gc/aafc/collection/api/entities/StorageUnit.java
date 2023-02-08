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
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class StorageUnit extends AbstractStorageUnit {

  public static final String TABLE_NAME = "storage_unit";
  public static final String ID_COLUMN_NAME = "id";
  public static final String UUID_COLUMN_NAME = "uuid";
  public static final String PARENT_ID_COLUMN_NAME = "parent_storage_unit_id";
  public static final String NAME_COLUMN_NAME = "name";
  public static final String TYPE_COLUMN_NAME = "storage_unit_type_id";

  public static final String HIERARCHY_PROP_NAME = "hierarchy";

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = PARENT_ID_COLUMN_NAME)
  private StorageUnit parentStorageUnit;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_storage_unit_id", referencedColumnName = "id", insertable = false, updatable = false)
  private List<ImmutableStorageUnit> storageUnitChildren = List.of();

  @Transient
  private List<StorageHierarchicalObject> hierarchy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = TYPE_COLUMN_NAME)
  private StorageUnitType storageUnitType;

}
