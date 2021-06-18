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

  private StorageUnit newUnit() {
    return StorageUnit.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy("dina")
      .build();
  }
}
