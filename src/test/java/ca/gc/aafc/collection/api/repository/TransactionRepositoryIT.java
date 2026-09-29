package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.dto.TransactionDto;
import ca.gc.aafc.collection.api.entities.AgentRolesWithDate;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ShipmentTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.TransactionFixture;
import ca.gc.aafc.dina.entity.AgentRoles;
import ca.gc.aafc.dina.exception.ConflictException;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.transaction.annotation.Transactional;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

public class TransactionRepositoryIT extends BaseRepositoryIT {

  @Inject
  private TransactionRepository transactionRepository;

  @Inject
  private CollectionControlledVocabularyItemRepository controlledVocabularyItemRepository;


  @WithMockKeycloakUser(username = "user", groupRole = TransactionFixture.GROUP + ":USER")
  @Test
  public void create_onValidData_transactionPersisted() throws ResourceNotFoundException, ResourceGoneException {
    TransactionDto transactionDto = TransactionFixture
        .newTransaction()
        .shipment(ShipmentTestFixture.newShipment().build())
        .agentRoles(new ArrayList<>(List.of(
          AgentRolesWithDate.builder()
            .agent(UUID.randomUUID())
            .roles(new ArrayList<>(List.of("Role1", "Role2")))
            .build(),
          AgentRolesWithDate.builder()
            .agent(UUID.randomUUID())
            .roles(new ArrayList<>(List.of("Role3")))
            .build()
        )))
        .build();

    JsonApiDocument transactionToCreate = JsonApiDocuments.createJsonApiDocument(
      null, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(transactionDto));

    UUID transactionId = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(
      transactionRepository.onCreate(transactionToCreate));

    TransactionDto createdTransaction = transactionRepository.getOne(transactionId, null).getDto();
    
    assertEquals(transactionId, createdTransaction.getUuid());
    assertNotNull(createdTransaction.getCreatedOn());
    assertEquals("user", createdTransaction.getCreatedBy());

    // Test roles.
    assertEquals(2, createdTransaction.getInvolvedAgents().size());
    assertEquals("Role1", createdTransaction.getAgentRoles().get(0).getRoles().get(0));
    assertEquals("Role2", createdTransaction.getAgentRoles().get(0).getRoles().get(1));
    assertEquals("Role3", createdTransaction.getAgentRoles().get(1).getRoles().get(0));

    assertEquals(ShipmentTestFixture.CURRENCY, createdTransaction.getShipment().getCurrency());

    // cleanup
    transactionRepository.onDelete(transactionId);
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = "wronggroup:USER")
  public void create_onWrongGroup_accessDenied() {
    TransactionDto transactionDto = TransactionFixture.newTransaction().build();
    JsonApiDocument transactionToCreate = JsonApiDocuments.createJsonApiDocument(
      null, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(transactionDto));
    Assertions
        .assertThrows(AccessDeniedException.class,
            () -> transactionRepository.onCreate(transactionToCreate));
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = TransactionFixture.GROUP + ":SUPER_USER")
  @Transactional
  public void create_onManagedAttributeValue_validationOccur() {

    CollectionControlledVocabularyItemDto newAttribute = CollectionControlledVocabularyItemTestFixture.newCollectionManagedAttribute2();
    newAttribute.setVocabularyElementType(VocabularyElementType.BOOL);
    newAttribute.setAcceptedValues(null);
    newAttribute.setGroup(TransactionFixture.GROUP);
    newAttribute.setDinaComponent(CollectionVocabularyConfiguration.DinaComponent.TRANSACTION.name());

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocumentWithRelToOne(
      null, CollectionControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(newAttribute),
      Map.of("controlledVocabulary", JsonApiDocument.ResourceIdentifier.builder()
        .type(CollectionControlledVocabularyDto.TYPENAME)
        .id(CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID).build()
      )
    );
  //  newAttribute = serviceTransactionWrapper.executeWithParam( (p) ->
  //    controlledVocabularyItemRepository.create(p, null).getDto(), docToCreate);

    // Create the managed attribute for bool

    String key = controlledVocabularyItemRepository.create(docToCreate, null).getDto().getKey();

    TransactionDto transactionDto = TransactionFixture.newTransaction()
        .managedAttributes( Map.of(key, "xyz"))
        .build();
    JsonApiDocument transactionToCreate = JsonApiDocuments.createJsonApiDocument(
      null, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(transactionDto));
    Assertions
        .assertThrows(ValidationException.class,
            () -> transactionRepository.onCreate(transactionToCreate));

    // fix the error and retry
    transactionDto.setManagedAttributes(Map.of(key, "true"));
    JsonApiDocument fixedTransactionToCreate = JsonApiDocuments.createJsonApiDocument(
      null, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(transactionDto));
    transactionRepository.onCreate(fixedTransactionToCreate);
  }

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = TransactionFixture.GROUP + ":SUPER_USER")
  public void save_onUpdateData_FieldsUpdated() throws ResourceNotFoundException, ResourceGoneException, ConflictException {
    final String updatedTransactionNumber = "Updated T2";
    TransactionDto transactionDto = TransactionFixture.newTransaction().build();
    JsonApiDocument transactionToCreate = JsonApiDocuments.createJsonApiDocument(
      null, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(transactionDto));
    UUID transactionId = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(
      transactionRepository.onCreate(transactionToCreate));
    TransactionDto createdTransaction = transactionRepository.getOne(transactionId, null).getDto();

    List<AgentRolesWithDate> agentRoleUpdate = createdTransaction.getAgentRoles();
    agentRoleUpdate.get(0).setRoles(new ArrayList<>(List.of("updatedRole")));

    createdTransaction.setTransactionNumber(updatedTransactionNumber);
    createdTransaction.setAgentRoles(agentRoleUpdate);

    JsonApiDocument transactionToUpdate = JsonApiDocuments.createJsonApiDocument(
      transactionId, TransactionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(createdTransaction));
    transactionRepository.onUpdate(transactionToUpdate, transactionId);

    TransactionDto loadedTransaction = transactionRepository.getOne(transactionId, null).getDto();
    assertEquals(updatedTransactionNumber, loadedTransaction.getTransactionNumber());
    assertEquals("updatedRole", loadedTransaction.getAgentRoles().get(0).getRoles().get(0));

    // cleanup
    transactionRepository.onDelete(transactionId);
  }


}
