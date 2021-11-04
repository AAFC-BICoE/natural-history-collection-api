package ca.gc.aafc.collection.api.service;

import java.util.UUID;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;

import io.crnk.core.exception.ResourceNotFoundException;

public class CollectionSequenceGeneratorServiceIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionSequenceGeneratorService collectionSequenceGeneratorService;

  private Collection collection;
  private CollectionSequence collectionSequence;

  @BeforeTransaction
  public void setUp() {
    service.runInNewTransaction(entityManager -> {
      collection = CollectionFactory.newCollection()
          .uuid(UUID.randomUUID())
          .group("testGroup")
          .build();

      entityManager.persist(collection);

      collectionSequence = CollectionSequence.builder()
          .collection(collection)
          .id(collection.getId())
          .build();
      entityManager.persist(collectionSequence);
    });
  }

  @Test
  void create_validRequest_getExpectedValues() {
    // Should start at zero and increment.
    CollectionSequenceGenerationRequest request = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(collection.getUuid())
        .build();
    Assertions.assertEquals(1, collectionSequenceGeneratorService.create(request).getResult().getLowReservedID());
    Assertions.assertEquals(2, collectionSequenceGeneratorService.create(request).getResult().getLowReservedID());
    Assertions.assertEquals(3, collectionSequenceGeneratorService.create(request).getResult().getLowReservedID());

    // Test incrementing by a higher amount.
    request.setAmount(20);
    CollectionSequenceReserved reservedIDs = collectionSequenceGeneratorService.create(request).getResult();
    Assertions.assertEquals(4, reservedIDs.getLowReservedID());
    Assertions.assertEquals(23, reservedIDs.getHighReservedID());
  }

  @Test
  void create_invalidRequest_exception() {
    UUID validCollectionID = collection.getUuid();

    // Request with a null collection id, this attribute always needs to be provided.
    CollectionSequenceGenerationRequest nullCollectionRequest = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(null)
        .build();

    // Request with a null collection id, this attribute always needs to be provided.
    CollectionSequenceGenerationRequest nullAmountRequest = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(validCollectionID)
        .amount(null)
        .build();

    // Request with a negative amount provided, this should fail.
    CollectionSequenceGenerationRequest negativeAmountRequest = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(validCollectionID)
        .amount(-1)
        .build();

    Assertions.assertThrows(ValidationException.class, () -> collectionSequenceGeneratorService.create(nullCollectionRequest));
    Assertions.assertThrows(ValidationException.class, () -> collectionSequenceGeneratorService.create(nullAmountRequest));
    Assertions.assertThrows(ValidationException.class, () -> collectionSequenceGeneratorService.create(negativeAmountRequest));
  }

  @Test
  void create_collectionDoesNotExist_exception() {
    // Request with a null collection id, this attribute always needs to be provided.
    CollectionSequenceGenerationRequest invalidCollectionRequest = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(UUID.randomUUID())
        .build();

    Assertions.assertThrows(ResourceNotFoundException.class, () -> collectionSequenceGeneratorService.create(invalidCollectionRequest));
  }

  @AfterTransaction
  public void tearDown() {
    service.runInNewTransaction(entityManager -> {
      entityManager.remove(entityManager.find(CollectionSequence.class, collectionSequence.getId()));
      entityManager.remove(entityManager.find(Collection.class, collection.getId()));
    });
  }
}
