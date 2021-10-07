package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class StorageUnitTypeCRUDIT extends CollectionModuleBaseIT {

  @Test
  void find() {
    StorageUnitType type = StorageUnitTypeFactory.newStorageUnitType()
      .build();

    storageUnitTypeService.create(type);
    StorageUnitType result = storageUnitTypeService.findOne(type.getUuid(), StorageUnitType.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(type.getName(), result.getName());
    Assertions.assertEquals(type.getGroup(), result.getGroup());
  }
}
