package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationMethodFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationMethodTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
  properties = "keycloak.enabled = true"
)
public class PreparationMethodRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private PreparationMethodRepository preparationMethodRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {PreparationMethodTestFixture.GROUP + ":DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationMethodDto pm = PreparationMethodTestFixture.newPreparationMethod();
    PreparationMethodDto result = preparationMethodRepository.findOne(
            preparationMethodRepository.create(pm).getUuid(),
            new QuerySpec(PreparationMethodDto.class));

    assertNotNull(result.getCreatedBy());
    assertEquals(pm.getName(), result.getName());
    assertEquals(pm.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied() {

    PreparationMethod testPreparationMethod = PreparationMethodFactory.newPreparationMethod()
      .group(PreparationMethodTestFixture.GROUP)
      .name(RandomStringUtils.randomAlphabetic(8))
      .build();
    preparationMethodService.create(testPreparationMethod);

    PreparationMethodDto retrievedPreparationType = preparationMethodRepository.findOne(testPreparationMethod.getUuid(),
      new QuerySpec(PreparationMethodDto.class));

    assertThrows(AccessDeniedException.class, () -> preparationMethodRepository.save(retrievedPreparationType));
  }

}
