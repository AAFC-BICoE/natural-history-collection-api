package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleActionDefinitionRepositoryIT extends CollectionModuleBaseIT {
  
  @Inject 
  private MaterialSampleActionDefinitionRepository materialSampleActionDefinitionRepository;
  
  private static final String GROUP = "aafc";
  private static final String name = "preparation process definition";
  public static final MaterialSampleActionDefinition.ActionType ACTION_TYPE = MaterialSampleActionDefinition.ActionType.ADD;


  @Test
  @WithMockKeycloakUser(groupRole = GROUP+":user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = newMaterialSampleActionDefinitionDto();
    MaterialSampleActionDefinitionDto result = materialSampleActionDefinitionRepository.findOne(
      materialSampleActionDefinitionRepository.create(materialSampleActionDefinitionDto).getUuid(),
      new QuerySpec(MaterialSampleActionDefinitionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(materialSampleActionDefinitionDto.getName(), result.getName());
    assertEquals(materialSampleActionDefinitionDto.getGroup(), result.getGroup());
    Assertions.assertEquals(ACTION_TYPE, result.getActionType());
  }

  private MaterialSampleActionDefinitionDto newMaterialSampleActionDefinitionDto() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setName(name);
    materialSampleActionDefinitionDto.setGroup(GROUP);
    materialSampleActionDefinitionDto.setUuid(UUID.randomUUID());
    materialSampleActionDefinitionDto.setActionType(ACTION_TYPE);
    materialSampleActionDefinitionDto.setFormTemplates(new HashMap<>(Map.of(MaterialSampleFormComponent.MATERIAL_SAMPLE, FormTemplate.builder()
      .allowNew(true)
      .allowExisting(true)
      .templateFields(new HashMap<>(Map.of("materialSampleName", TemplateField.builder()
        .enabled(true)  
        .defaultValue("test-default-value")
        .build())))
      .build())));
    return materialSampleActionDefinitionDto;
  }
}
