package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

  @Test
  void create_linkParentThroughParent() {
    StorageUnit child = newUnit();
    storageUnitService.create(child);

    StorageUnit parent = newUnit();
    parent.setStorageUnitChildren(List.of(storageUnitService.findOne(child.getUuid(), StorageUnit.class)));
    storageUnitService.create(parent);

    StorageUnit result = storageUnitService.findOne(child.getUuid(), StorageUnit.class);
    Assertions.assertEquals(parent.getUuid(), result.getParentStorageUnit().getUuid());

    StorageUnit parentResult = storageUnitService.findOne(parent.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(parentResult.getStorageUnitChildren());
    Assertions.assertEquals(1, parentResult.getStorageUnitChildren().size());
    Assertions.assertEquals(child.getUuid(), parentResult.getStorageUnitChildren().get(0).getUuid());
  }

  @Test
  void update_linkParentThroughChild() {
    StorageUnit parent = newUnit();
    storageUnitService.create(parent);
    StorageUnit child = newUnit();
    storageUnitService.create(child);

    StorageUnit toUpdate = storageUnitService.findOne(child.getUuid(), StorageUnit.class);
    toUpdate.setParentStorageUnit(parent);
    storageUnitService.update(toUpdate);

    StorageUnit result = storageUnitService.findOne(toUpdate.getUuid(), StorageUnit.class);
    Assertions.assertEquals(parent.getUuid(), result.getParentStorageUnit().getUuid());

    StorageUnit parentResult = storageUnitService.findOne(parent.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(parentResult.getStorageUnitChildren());
    Assertions.assertEquals(1, parentResult.getStorageUnitChildren().size());
    Assertions.assertEquals(child.getUuid(), parentResult.getStorageUnitChildren().get(0).getUuid());
  }

  @Test
  void update_linkParentThroughParent() {
    StorageUnit parent = newUnit();
    storageUnitService.create(parent);
    StorageUnit child = newUnit();
    storageUnitService.create(child);

    StorageUnit toUpdate = storageUnitService.findOne(parent.getUuid(), StorageUnit.class);
    toUpdate.setStorageUnitChildren(new ArrayList<>());
    toUpdate.getStorageUnitChildren().add(storageUnitService.findOne(child.getUuid(), StorageUnit.class));
    storageUnitService.update(toUpdate);

    StorageUnit result = storageUnitService.findOne(child.getUuid(), StorageUnit.class);
    Assertions.assertEquals(parent.getUuid(), result.getParentStorageUnit().getUuid());

    StorageUnit parentResult = storageUnitService.findOne(parent.getUuid(), StorageUnit.class);
    Assertions.assertNotNull(parentResult.getStorageUnitChildren());
    Assertions.assertEquals(1, parentResult.getStorageUnitChildren().size());
    Assertions.assertEquals(child.getUuid(), parentResult.getStorageUnitChildren().get(0).getUuid());
  }

  @Test
  void deleteChild() {
    StorageUnit parent = newUnit();
    storageUnitService.create(parent);
    StorageUnit child = newUnit();
    child.setParentStorageUnit(parent);
    storageUnitService.create(child);

    Assertions.assertEquals(
      parent.getUuid(),
      storageUnitService.findOne(child.getUuid(), StorageUnit.class).getParentStorageUnit().getUuid());

    storageUnitService.delete(child);
    Assertions.assertTrue(CollectionUtils.isEmpty(
      storageUnitService.findOne(parent.getUuid(), StorageUnit.class).getStorageUnitChildren()));
  }

  @Test
  void deleteParent() {
    StorageUnit parent = newUnit();
    storageUnitService.create(parent);
    StorageUnit child = newUnit();
    child.setParentStorageUnit(parent);
    storageUnitService.create(child);

    Assertions.assertEquals(
      parent.getUuid(),
      storageUnitService.findOne(child.getUuid(), StorageUnit.class).getParentStorageUnit().getUuid());

    storageUnitService.delete(parent);
    Assertions.assertNull(
      storageUnitService.findOne(child.getUuid(), StorageUnit.class).getParentStorageUnit());
  }

  private StorageUnit newUnit() {
    return StorageUnit.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .group(RandomStringUtils.randomAlphabetic(5))
      .createdBy("dina")
      .build();
  }
}
