package ca.gc.aafc.collection.api.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;

import org.hibernate.annotations.NaturalIdCache;

import ca.gc.aafc.collection.api.service.StorageHierarchicalObject;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
@NaturalIdCache
public class StorageUnit extends AbstractStorageUnit {

  @Builder
  public StorageUnit(
      Integer id, 
      UUID uuid, 
      String name, 
      String group, 
      OffsetDateTime createdOn, 
      String createdBy, 
      StorageUnit parentStorageUnit, 
      List<ImmutableStorageUnitChild> storageUnitChildren, 
      List<StorageHierarchicalObject> hierarchy, 
      StorageUnitType storageUnitType
  ) {
    super(id, uuid, name, group, createdOn, createdBy, parentStorageUnit, storageUnitChildren, hierarchy, storageUnitType);
  }
}
