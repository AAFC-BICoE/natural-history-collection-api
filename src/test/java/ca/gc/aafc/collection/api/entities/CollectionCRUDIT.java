package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.CollectionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class CollectionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionService collectionService;

  @Test
  void create() {
    Collection collection = Collection.builder()
      .name("name")
      .group("group")
      .code("code")
      .createdBy("someone")
      .build();
    collectionService.create(collection);
    Assertions.assertNotNull(collection.getUuid());

    Collection result = collectionService.findOne(collection.getUuid(), Collection.class);
    Assertions.assertNotNull(collection.getCreatedOn());
    Assertions.assertNotNull(collection.getId());
    Assertions.assertEquals(collection.getName(), result.getName());
    Assertions.assertEquals(collection.getGroup(), result.getGroup());
    Assertions.assertEquals(collection.getCode(), result.getCode());
    Assertions.assertEquals(collection.getCreatedBy(), result.getCreatedBy());
  }

}