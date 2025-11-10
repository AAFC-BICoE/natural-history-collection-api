package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.service.ProtocolService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.ProtocolFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

public class ProtocolRepositoryIT extends BaseRepositoryIT {

  @Inject
  private ProtocolRepository protocolRepository;

  @Inject
  protected ProtocolService protocolService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {ProtocolTestFixture.GROUP + ":DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {

    ProtocolDto protocolDto = ProtocolTestFixture.newProtocol();
    UUID protocolUUID = createWithRepository(protocolDto, protocolRepository::onCreate);

    ProtocolDto result = protocolRepository.getOne(protocolUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(protocolDto.getName(), result.getName());
    assertEquals(protocolDto.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {ProtocolTestFixture.GROUP + ":user"})
  public void updateFromNonAdmin_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    Protocol testProtocol = ProtocolFactory.newProtocol()
            .group(ProtocolTestFixture.GROUP)
            .name("aafc")
            .build();
    serviceTransactionWrapper.execute( protocolService::create, testProtocol);

    ProtocolDto retrievedProtocol = protocolRepository.getOne(testProtocol.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedProtocol.getUuid(), CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedProtocol)
    );

    assertThrows(AccessDeniedException.class, () -> protocolRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }
}

