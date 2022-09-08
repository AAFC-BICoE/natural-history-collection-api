package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(
  properties = "keycloak.enabled = true"
)
public class PreparationTypeRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private PreparationTypeRepository preparationTypeRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationTypeDto pt = PreparationTypeTestFixture.newPreparationType();
    PreparationTypeDto result = preparationTypeRepository.findOne(
      preparationTypeRepository.create(pt).getUuid(),
      new QuerySpec(PreparationTypeDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied() {
    PreparationType testPreparationType = PreparationTypeFactory.newPreparationType()
      .group("preparation process definition")
      .name("aafc")
      .build();
    preparationTypeService.create(testPreparationType);
    PreparationTypeDto retrievedPreparationType = preparationTypeRepository.findOne(testPreparationType.getUuid(),
      new QuerySpec(PreparationTypeDto.class));
    assertThrows(AccessDeniedException.class, () -> preparationTypeRepository.save(retrievedPreparationType));
  }

}
