package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionSequenceGeneratorRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionSequenceGeneratorRepository collectionSequenceRepository;

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void reserveNewSequenceIds_collectionExists_idReserved() {
    // Create a collection to generate sequences from.
    Collection persistCollection = Collection.builder()
        .uuid(UUID.randomUUID())
        .name("name")
        .group("group")
        .createdBy("by")
        .code("DNA")
        .build();
    collectionService.create(persistCollection);
    assertNotNull(persistCollection.getUuid());
    assertNotNull(persistCollection.getId());

    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(persistCollection.getUuid());

    // Send the request to the repository.
    CollectionSequenceGeneratorDto responseSequence = collectionSequenceRepository.create(requestSequence);
    assertEquals(1, responseSequence.getResult().getLowReservedID());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void reserveNewSequenceIds_collectionDoesNotExist_exception() {
    // Generate a collection sequence dto request with invalid collection uuid.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(UUID.randomUUID());

    // Send the request to the repository expecting a violation exception.
    assertThrows(ValidationException.class, () -> collectionSequenceRepository.create(requestSequence));
  }

}
