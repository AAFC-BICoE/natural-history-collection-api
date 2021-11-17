package ca.gc.aafc.collection.api.repository;

import java.util.UUID;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.exception.ResourceNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionSequenceGeneratorRepositoryIT extends CollectionModuleBaseIT {

  private static final String VALID_GROUP = "aafc";
  private static final String INVALID_GROUP = "notvalid";

  @Inject
  private CollectionSequenceGeneratorRepository collectionSequenceRepository;

  private Collection collection;
  private CollectionSequence collectionSequence;

  @BeforeTransaction
  public void setUp() {
    service.runInNewTransaction(entityManager -> {
      collection = CollectionFactory.newCollection()
          .uuid(UUID.randomUUID())
          .group(VALID_GROUP)
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
  @WithMockKeycloakUser(groupRole = VALID_GROUP + ": staff")
  public void reserveNewSequenceIds_accessGranted_idReserved() {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(collection.getUuid());

    // Send the request to the repository.
    CollectionSequenceGeneratorDto responseSequence = collectionSequenceRepository.create(requestSequence);
    assertEquals(1, responseSequence.getResult().getLowReservedID());
  }

  @Test
  @WithMockKeycloakUser(groupRole = VALID_GROUP + ": staff")
  public void reserveNewSequenceIds_collectionDoesNotExist_exception() {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(UUID.randomUUID());

    // Send the request to the repository, since the collection does not exist, it should throw an exception.
    assertThrows(ResourceNotFoundException.class, () -> collectionSequenceRepository.create(requestSequence));
  }

  @Test
  @WithMockKeycloakUser(groupRole = INVALID_GROUP + ": staff")
  public void reserveNewSequenceIds_accessDenied_exception() {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(collection.getUuid());

    // Send the request to the repository, should result in an AccessDeniedException.
    assertThrows(AccessDeniedException.class, () -> collectionSequenceRepository.create(requestSequence));
  }

  @AfterTransaction
  public void tearDown() {
    service.runInNewTransaction(entityManager -> {
      entityManager.remove(entityManager.find(CollectionSequence.class, collectionSequence.getId()));
      entityManager.remove(entityManager.find(Collection.class, collection.getId()));
    });
  }
}
