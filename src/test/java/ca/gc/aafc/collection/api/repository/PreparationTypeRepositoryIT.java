package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
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

public class PreparationTypeRepositoryIT extends BaseRepositoryIT {

  @Inject
  private PreparationTypeRepository preparationTypeRepository;

  @Inject
  private PreparationTypeService preparationTypeService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {

    PreparationTypeDto pt = PreparationTypeTestFixture.newPreparationType();
    UUID prepTypeUUID = createWithRepository(pt, preparationTypeRepository::onCreate);
    PreparationTypeDto result = preparationTypeRepository.getOne(prepTypeUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    PreparationType testPreparationType = PreparationTypeFactory.newPreparationType()
      .group("preparation process definition")
      .name("aafc")
      .build();

    serviceTransactionWrapper.execute(preparationTypeService::create, testPreparationType);

    PreparationTypeDto retrievedPreparationType = preparationTypeRepository.getOne(testPreparationType.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedPreparationType.getJsonApiId(), retrievedPreparationType.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(retrievedPreparationType)
    );
    assertThrows(AccessDeniedException.class, () -> preparationTypeRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }
}
