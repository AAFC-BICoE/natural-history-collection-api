package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

class CollectionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private InstitutionService institutionService;

  @Test
  void create() {
    Collection collection = CollectionFactory.newCollection()
      .institution(institutionService.create(InstitutionFixture.newInstitutionEntity().build()))
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
    Assertions.assertEquals(collection.getInstitution().getUuid(), result.getInstitution().getUuid());
  }

}