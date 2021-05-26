package ca.gc.aafc.collection.api.repository;

import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.crnk.core.queryspec.QuerySpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleActionDefinitionRepositoryIT extends CollectionModuleBaseIT {
  
  @Inject 
  private MaterialSampleActionDefinitionRepository materialSampleActionDefinitionRepository;
  
  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser(username = "test user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = newMaterialSampleActionDefinitionDto();
    MaterialSampleActionDefinitionDto result = materialSampleActionDefinitionRepository.findOne(
      materialSampleActionDefinitionRepository.create(materialSampleActionDefinitionDto).getUuid(),
      new QuerySpec(MaterialSampleActionDefinitionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(materialSampleActionDefinitionDto.getName(), result.getName());
    assertEquals(materialSampleActionDefinitionDto.getGroup(), result.getGroup());
  }

  private MaterialSampleActionDefinitionDto newMaterialSampleActionDefinitionDto() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setName(name);
    materialSampleActionDefinitionDto.setGroup(group);
    materialSampleActionDefinitionDto.setUuid(UUID.randomUUID());
    return materialSampleActionDefinitionDto;
  }
}
