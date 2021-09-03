package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class CollectionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private InstitutionService institutionService;

  @Test
  void create() {
    Institution institution = institutionService.create(Institution.builder()
      .name(RandomStringUtils.randomAlphabetic(5))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .build());
    Collection collection = CollectionFactory.newCollection().institution(institution).build();
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