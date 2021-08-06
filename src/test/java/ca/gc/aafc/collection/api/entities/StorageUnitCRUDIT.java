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
  void createParentChild() {
    StorageUnit parentUnit = StorageUnitFactory.newStorageUnit()
        .build();
    storageUnitService.create(parentUnit);

    StorageUnit childUnit = StorageUnitFactory.newStorageUnit()
        .build();
    storageUnitService.create(childUnit);

    //Set relationship
    childUnit.setParentStorageUnit(parentUnit);
    storageUnitService.update(childUnit);

    StorageUnit resultParent = storageUnitService.findOne(parentUnit.getUuid(), StorageUnit.class);

    Assertions.assertEquals(1, resultParent.getStorageUnitChildren().size());
    Assertions.assertEquals(childUnit.getUuid(), resultParent.getStorageUnitChildren().get(0).getUuid());
  }

}
