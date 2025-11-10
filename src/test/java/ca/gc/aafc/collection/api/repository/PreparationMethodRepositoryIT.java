package ca.gc.aafc.collection.api.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.collection.api.service.PreparationMethodService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationMethodFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationMethodTestFixture;
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

public class PreparationMethodRepositoryIT extends BaseRepositoryIT {

  @Inject
  private PreparationMethodRepository preparationMethodRepository;

  @Inject
  private PreparationMethodService preparationMethodService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {PreparationMethodTestFixture.GROUP + ":DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {

    PreparationMethodDto pm = PreparationMethodTestFixture.newPreparationMethod();
    UUID prepMethodUUID = createWithRepository(pm, preparationMethodRepository::onCreate);

    PreparationMethodDto result = preparationMethodRepository.getOne(prepMethodUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(pm.getName(), result.getName());
    assertEquals(pm.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {

    PreparationMethod testPreparationMethod = PreparationMethodFactory.newPreparationMethod()
      .group(PreparationMethodTestFixture.GROUP)
      .name(RandomStringUtils.randomAlphabetic(8))
      .build();
    serviceTransactionWrapper.execute(preparationMethodService::create, testPreparationMethod);

    PreparationMethodDto retrievedPreparationMethod = preparationMethodRepository.getOne(testPreparationMethod.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedPreparationMethod.getJsonApiId(), retrievedPreparationMethod.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(retrievedPreparationMethod)
    );

    assertThrows(AccessDeniedException.class, () -> preparationMethodRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }

}
