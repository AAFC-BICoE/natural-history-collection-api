package ca.gc.aafc.collection.api.service;

import java.util.UUID;
import javax.inject.Inject;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper.CollectionSequenceReserved;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;

import io.crnk.core.exception.ResourceNotFoundException;

public class CollectionSequenceGeneratorServiceIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionSequenceGeneratorService collectionSequenceGeneratorService;

  @Test
  void create_validRequest_getExpectedValues() {
    // Should start at zero and increment.
    CollectionSequenceGenerationRequest request = CollectionSequenceGenerationRequest.builder()
        .group("testGroup")
        .collectionId(persistCollectionGetUUID())
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
    UUID validCollectionID = persistCollectionGetUUID();

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

  private UUID persistCollectionGetUUID() {
    Collection collection = CollectionFactory.newCollection().build();
    collectionService.create(collection);

    // ugly hack until we can do service.flush
    // update will flush so the mapper can see the record
    collectionService.update(collection);

    return collection.getUuid();
  }

}
