package ca.gc.aafc.collection.api.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

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
  @WithMockKeycloakUser(groupRole = {"aafc: staff"})
  public void reserveNewSequenceIds_collectionExists_idReserved() {
    // Create a collection to generate sequences from.
    Collection persistCollection = Collection.builder()
        .uuid(UUID.randomUUID())
        .name("name")
        .group("aafc")
        .createdBy("createdBy")
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

  // TODO create new test with invalid access.

}
