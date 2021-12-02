package ca.gc.aafc.collection.api.entities;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;

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
    Assertions.assertEquals(
      collection.getMultilingualDescription().getDescriptions().get(0).getLang(),
      result.getMultilingualDescription().getDescriptions().get(0).getLang());
  }

  @Test
  void newline_contact_and_address() {
    Collection collection = collectionService.create(CollectionFactory.newCollection()
      .address("line1\nline2")
      .contact("line1\nline2")
      .build());

    Assertions.assertTrue(
      collection.getAddress().contains("\n"));
    Assertions.assertTrue(
      collection.getContact().contains("\n"));
  }

  @Test
  void testInvalidURLValidation_throwsConstraintValidation() {
    Collection collection = CollectionFactory.newCollection().webpage("invalidurl").build();

    Assertions.assertThrows(ConstraintViolationException.class, () -> collectionService.create(collection));
  }

  @Test
  void onCreateCollection_collectionSequenceCreated() {
    Collection collection = collectionService.create(CollectionFactory.newCollection().build());

    // Ensure that a collection sequence has been created when a collection is created.
    Assertions.assertNotNull(collectionSequenceService.findOneById(collection.getId(), CollectionSequence.class));
  }

  @Test
  void onDeleteCollection_collectionSequenceDeleted() {
    Collection collection = CollectionFactory.newCollection().build();
    collectionService.create(collection);
    int collectionID = collection.getId();

    // Ensure that a collection sequence has been created when a collection is created.
    Assertions.assertNotNull(collectionSequenceService.findOneById(collectionID, CollectionSequence.class));

    // Delete the collection
    collectionService.delete(collection);

    // Since the record has been deleted, the Cascade type on the one to one
    // relationship should automatically delete the CollectionSequence.
    Assertions.assertNull(collectionSequenceService.findOneById(collectionID, CollectionSequence.class));
  }

  @Test
  void create_ParentHasParent_ThrowsValidationException() {

    Collection collection = CollectionFactory.newCollection().build();
    Collection parentCollection = CollectionFactory.newCollection().build();
    Collection parentParentCollection = CollectionFactory.newCollection().build();
    collection.setParentCollection(parentCollection);
    parentCollection.setParentCollection(parentParentCollection);

    Assertions.assertThrows(ValidationException.class, 
      () -> collectionService.create(collection));

  }
}
