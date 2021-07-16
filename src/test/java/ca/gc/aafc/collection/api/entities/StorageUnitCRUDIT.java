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

  @Test
  void circularReference_ThrowsException() {
    StorageUnit a = StorageUnitFactory.newStorageUnit()
      .build();
    storageUnitService.create(a);

    StorageUnit b = StorageUnitFactory.newStorageUnit()
      .build();
    b.setParentStorageUnit(a);
    storageUnitService.create(b);

    StorageUnit c = StorageUnitFactory.newStorageUnit()
      .build();
    c.setParentStorageUnit(b);
    storageUnitService.create(c);

    StorageUnit update_a = storageUnitService.findOne(a.getUuid(), StorageUnit.class);
    update_a.setParentStorageUnit(b);

    // A -> B -> C -> A
    storageUnitService.update(update_a);
  }
}
