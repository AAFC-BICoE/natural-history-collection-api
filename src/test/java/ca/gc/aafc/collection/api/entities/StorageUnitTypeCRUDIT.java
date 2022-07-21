package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;

import ca.gc.aafc.collection.api.testsupport.fixtures.StorageGridLayoutTestFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StorageUnitTypeCRUDIT extends CollectionModuleBaseIT {

  @Test
  void find() {
    StorageUnitType type = StorageUnitTypeFactory.newStorageUnitType()
            .gridLayoutDefinition(StorageGridLayoutTestFixture.newStorageGridLayout())
            .build();

    storageUnitTypeService.create(type);
    StorageUnitType result = storageUnitTypeService.findOne(type.getUuid(), StorageUnitType.class);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedOn());
    assertEquals(type.getName(), result.getName());
    assertEquals(type.getGroup(), result.getGroup());
    assertNotNull(result.getGridLayoutDefinition());

  }
}
