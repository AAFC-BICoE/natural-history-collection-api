package ca.gc.aafc.collection.api.repository;

import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.PreparationProcessDefinitionDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.crnk.core.queryspec.QuerySpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationProcessDefinitionRepositoryIT extends CollectionModuleBaseIT {
  
  @Inject 
  private PreparationProcessDefinitionRepository preparationProcessDefinitionRepository;
  
  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser(username = "test user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationProcessDefinitionDto ppd = newPreparationProcessDefinitionDto();
    PreparationProcessDefinitionDto result = preparationProcessDefinitionRepository.findOne(
      preparationProcessDefinitionRepository.create(ppd).getUuid(),
      new QuerySpec(PreparationProcessDefinitionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(ppd.getName(), result.getName());
    assertEquals(ppd.getGroup(), result.getGroup());
  }

  private PreparationProcessDefinitionDto newPreparationProcessDefinitionDto() {
    PreparationProcessDefinitionDto ppd = new PreparationProcessDefinitionDto();
    ppd.setName(name);
    ppd.setGroup(group);
    ppd.setUuid(UUID.randomUUID());
    return ppd;
  }
}
