package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.service.ExpeditionService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ExpeditionTestFixture;
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

public class ExpeditionRepositoryIT extends BaseRepositoryIT {

  @Inject
  private ExpeditionRepository expeditionRepository;

  @Inject
  private ExpeditionService expeditionService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {
    ExpeditionDto expeditionDto = ExpeditionTestFixture.newExpedition();
    UUID expeditionUUID = createWithRepository(expeditionDto, expeditionRepository::onCreate);

    ExpeditionDto result = expeditionRepository.getOne(expeditionUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(expeditionDto.getName(), result.getName());
    assertEquals(expeditionDto.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    Expedition testExpedition = ExpeditionFactory.newExpedition()
      .group("preparation process definition")
      .name("aafc")
      .build();
    serviceTransactionWrapper.execute( expeditionService::create, testExpedition);

    ExpeditionDto retrievedExpedition = expeditionRepository.getOne(testExpedition.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedExpedition.getUuid(), CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedExpedition)
    );

    assertThrows(AccessDeniedException.class, () -> expeditionRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }
}
