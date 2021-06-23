package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StorageUnitCRUDIT extends CollectionModuleBaseIT {

  @Test
  void create() {
    StorageUnit unit = StorageUnitFactory.newStorageUnit()
      .build();
    storageUnitService.create(unit);

    StorageUnit result = storageUnitService.findOne(unit.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(unit.getName(), result.getName());
    Assertions.assertEquals(unit.getGroup(), result.getGroup());
  }

}
