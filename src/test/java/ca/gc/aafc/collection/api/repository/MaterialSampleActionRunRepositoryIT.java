package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionRunDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleActionRunRepositoryIT extends CollectionModuleBaseIT {

  @Inject 
  private MaterialSampleActionRunRepository materialSampleActionRunRepository;

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject 
  private MaterialSampleActionDefinitionRepository materialSampleActionDefinitionRepository;

  private static final String dwcCatalogNumber = "R-4313";
  private static final String GROUP = "aafc";
  private static final String name = "preparation process definition";
  private static final LocalDateTime startDateTime= LocalDateTime.of(2007, 12, 3, 10, 15, 30);
  private static final LocalDateTime endDateTime= LocalDateTime.of(2019, 12, 3, 10, 15, 30);
  
  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = GROUP + ":staff")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleDto ms = materialSampleRepository.findOne(
      materialSampleRepository.create(newMaterialSampleDto(null)).getUuid(), new QuerySpec(MaterialSampleDto.class));
    MaterialSampleActionDefinitionDto msad = materialSampleActionDefinitionRepository.findOne(
      materialSampleActionDefinitionRepository.create(newMaterialSampleActionDefinitionDto()).getUuid(),
      new QuerySpec(MaterialSampleActionDefinitionDto.class));

    MaterialSampleActionRunDto msar = newMaterialSampleActionRunDto(ms, msad);
    MaterialSampleActionRunDto result = materialSampleActionRunRepository.findOne(
      materialSampleActionRunRepository.create(msar).getUuid(),
      new QuerySpec(MaterialSampleActionRunDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(msar.getAgentId(), result.getAgentId());
    assertEquals(msar.getStartDateTime(), result.getStartDateTime());
    assertEquals(msar.getEndDateTime(), result.getEndDateTime());
    assertEquals(msar.getMaterialSampleActionDefinition().getUuid(), result.getMaterialSampleActionDefinition().getUuid());
    assertEquals(msar.getSourceMaterialSample().getUuid(), result.getSourceMaterialSample().getUuid());
    assertEquals(msar.getGroup(), result.getGroup());
  }

  private MaterialSampleActionRunDto newMaterialSampleActionRunDto(
    MaterialSampleDto materialSampleDto,
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto) {
      MaterialSampleActionRunDto materialSampleActionRunDto = new MaterialSampleActionRunDto();
      materialSampleActionRunDto.setStartDateTime(startDateTime);
      materialSampleActionRunDto.setEndDateTime(endDateTime);
      materialSampleActionRunDto.setGroup(GROUP);
      materialSampleActionRunDto.setAgentId(UUID.randomUUID());
      materialSampleActionRunDto.setSourceMaterialSample(materialSampleDto);
      materialSampleActionRunDto.setMaterialSampleActionDefinition(materialSampleActionDefinitionDto);
      materialSampleActionRunDto.setUuid(UUID.randomUUID());
    return materialSampleActionRunDto;
  }

  private MaterialSampleActionDefinitionDto newMaterialSampleActionDefinitionDto() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setName(name);
    materialSampleActionDefinitionDto.setGroup(GROUP);
    materialSampleActionDefinitionDto.setUuid(UUID.randomUUID());
    materialSampleActionDefinitionDto.setCreatedBy("test user");
    materialSampleActionDefinitionDto.setActionType(MaterialSampleActionDefinition.ActionType.ADD);
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
  
  private MaterialSampleDto newMaterialSampleDto(
    CollectingEventDto event) {
    MaterialSampleDto materialSampleDto = new MaterialSampleDto();
    materialSampleDto.setDwcCatalogNumber(dwcCatalogNumber);
    materialSampleDto.setCollectingEvent(event);
    materialSampleDto.setGroup(GROUP);
    materialSampleDto.setUuid(UUID.randomUUID());
    materialSampleDto.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return materialSampleDto;
  }
}
