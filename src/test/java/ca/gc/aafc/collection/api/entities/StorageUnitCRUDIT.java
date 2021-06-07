package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;

class StorageUnitCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitService storageUnitService;

  @Test
  void create() {
    StorageUnit unit = newUnit();
    storageUnitService.create(unit);

    StorageUnit result = storageUnitService.findOne(unit.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(unit.getName(), result.getName());
    Assertions.assertEquals(unit.getGroup(), result.getGroup());
  }

  @Test
  void create_linkParentThroughChild() {
    StorageUnit parent = newUnit();
    storageUnitService.create(parent);

    StorageUnit child = newUnit();
    child.setParentStorageUnit(parent);
    storageUnitService.create(child);

    StorageUnit result = storageUnitService.findOne(child.getUuid(), StorageUnit.class);
    Assertions.assertEquals(parent.getUuid(), result.getParentStorageUnit().getUuid());

    StorageUnit parentResult = storageUnitService.findOne(parent.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(parentResult.getStorageUnitChildren());
    Assertions.assertEquals(1, parentResult.getStorageUnitChildren().size());
    Assertions.assertEquals(child.getUuid(), parentResult.getStorageUnitChildren().get(0).getUuid());
  }

  private StorageUnit newUnit() {
    return StorageUnit.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy("dina")
      .build();
  }
}
