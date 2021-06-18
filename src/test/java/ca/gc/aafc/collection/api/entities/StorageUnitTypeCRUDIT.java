package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;

class StorageUnitTypeCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitTypeService typeService;

  @Test
  void find() {
    StorageUnitType type = StorageUnitType.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy("dina")
      .build();

    typeService.create(type);
    StorageUnitType result = typeService.findOne(type.getUuid(), StorageUnitType.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(type.getName(), result.getName());
    Assertions.assertEquals(type.getGroup(), result.getGroup());
  }
}
