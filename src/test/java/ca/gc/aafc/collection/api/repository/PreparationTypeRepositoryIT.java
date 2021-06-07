package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationTypeRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private PreparationTypeRepository preparationTypeRepository;

  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser(username = "user", groupRole = {"aafc: staff"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationTypeDto pt = newPreparationTypeDto();
    PreparationTypeDto result = preparationTypeRepository.findOne(
      preparationTypeRepository.create(pt).getUuid(),
      new QuerySpec(PreparationTypeDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
  public void updateFromDifferentGroup_throwAccessDenied() {
      PreparationType testPreparationType = PreparationTypeFactory.newPreparationType()
        .group(group)
        .name(name)
        .build();
      preparationTypeService.create(testPreparationType);
      PreparationTypeDto retrievedPreparationType = preparationTypeRepository.findOne(testPreparationType.getUuid(),
          new QuerySpec(PreparationTypeDto.class));
      assertThrows(AccessDeniedException.class, () -> preparationTypeRepository.save(retrievedPreparationType));
  }

  private PreparationTypeDto newPreparationTypeDto() {
    PreparationTypeDto pt = new PreparationTypeDto();
    pt.setName(name);
    pt.setGroup(group);
    pt.setUuid(UUID.randomUUID());
    return pt;
  }
  
}
