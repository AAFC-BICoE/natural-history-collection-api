package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionFactory;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionSequenceGeneratorRepositoryIT extends CollectionModuleBaseIT {

  private static final String VALID_GROUP = "aafc";
  private static final String INVALID_GROUP = "notvalid";

  @Autowired
  private ObjectMapper objectMapper;

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
  @WithMockKeycloakUser(groupRole = VALID_GROUP + ":user")
  public void reserveNewSequenceIds_accessGranted_idReserved() throws Exception {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(collection.getUuid());

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, requestSequence.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(requestSequence)
    );

    String jsonResponse = objectMapper.writeValueAsString(collectionSequenceRepository.onCreate(docToCreate).getBody());
    CollectionSequenceGeneratorDto responseSequence = objectMapper.readValue(jsonResponse, CollectionSequenceGeneratorDto.class);
    assertEquals(1, responseSequence.getResult().getLowReservedID());
  }

  @Test
  @WithMockKeycloakUser(groupRole = VALID_GROUP + ":user")
  public void reserveNewSequenceIds_collectionDoesNotExist_exception() {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(UUID.randomUUID());

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, requestSequence.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(requestSequence)
    );

    // Send the request to the repository, since the collection does not exist, it should throw an exception.
    assertThrows(ResourceNotFoundException.class, () -> collectionSequenceRepository.onCreate(docToCreate));
  }

  @Test
  @WithMockKeycloakUser(groupRole = INVALID_GROUP + ":user")
  public void reserveNewSequenceIds_accessDenied_exception() {
    // Generate a collection sequence dto request.
    CollectionSequenceGeneratorDto requestSequence = new CollectionSequenceGeneratorDto();
    requestSequence.setAmount(1);
    requestSequence.setCollectionId(collection.getUuid());

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, requestSequence.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(requestSequence)
    );

    // Send the request to the repository, should result in an AccessDeniedException.
    assertThrows(AccessDeniedException.class, () -> collectionSequenceRepository.onCreate(docToCreate));
  }

  @AfterTransaction
  public void tearDown() {
    service.runInNewTransaction(entityManager -> {
      entityManager.remove(entityManager.find(CollectionSequence.class, collectionSequence.getId()));
      entityManager.remove(entityManager.find(Collection.class, collection.getId()));
    });
  }
}
