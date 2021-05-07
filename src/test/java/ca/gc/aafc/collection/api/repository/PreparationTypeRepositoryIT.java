package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationTypeRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private PreparationTypeRepository preparationTypeRepository;

  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationTypeDto pt = newPreparationTypeDto();
    PreparationTypeDto result = preparationTypeRepository.findOne(
      preparationTypeRepository.create(pt).getUuid(),
      new QuerySpec(PreparationTypeDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  private PreparationTypeDto newPreparationTypeDto() {
    PreparationTypeDto pt = new PreparationTypeDto();
    pt.setName(name);
    pt.setGroup(group);
    pt.setUuid(UUID.randomUUID());
    return pt;
  }
  
}
