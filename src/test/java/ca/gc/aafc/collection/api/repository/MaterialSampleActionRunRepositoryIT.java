package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionDefinitionDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleActionRunDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.crnk.core.queryspec.QuerySpec;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleActionRunRepositoryIT extends CollectionModuleBaseIT {

  @Inject 
  private MaterialSampleActionRunRepository materialSampleActionRunRepository;

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject 
  private MaterialSampleActionDefinitionRepository materialSampleActionDefinitionRepository;

  private static final String dwcCatalogNumber = "R-4313";
  private static final String group = "aafc";
  private static final String name = "preparation process definition";
  private static final LocalDateTime startDateTime= LocalDateTime.of(2007, 12, 3, 10, 15, 30);
  private static final LocalDateTime endDateTime= LocalDateTime.of(2019, 12, 3, 10, 15, 30);
  
  @Test
  @WithMockKeycloakUser(username = "test user")
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
  }

  private MaterialSampleActionRunDto newMaterialSampleActionRunDto(
    MaterialSampleDto materialSampleDto,
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto) {
      MaterialSampleActionRunDto materialSampleActionRunDto = new MaterialSampleActionRunDto();
      materialSampleActionRunDto.setStartDateTime(startDateTime);
      materialSampleActionRunDto.setEndDateTime(endDateTime);
      materialSampleActionRunDto.setAgentId(UUID.randomUUID());
      materialSampleActionRunDto.setSourceMaterialSample(materialSampleDto);
      materialSampleActionRunDto.setMaterialSampleActionDefinition(materialSampleActionDefinitionDto);
      materialSampleActionRunDto.setUuid(UUID.randomUUID());
    return materialSampleActionRunDto;
  }

  private MaterialSampleActionDefinitionDto newMaterialSampleActionDefinitionDto() {
    MaterialSampleActionDefinitionDto materialSampleActionDefinitionDto = new MaterialSampleActionDefinitionDto();
    materialSampleActionDefinitionDto.setName(name);
    materialSampleActionDefinitionDto.setGroup(group);
    materialSampleActionDefinitionDto.setUuid(UUID.randomUUID());
    materialSampleActionDefinitionDto.setCreatedBy("test user");
    return materialSampleActionDefinitionDto;
  }
  
  private MaterialSampleDto newMaterialSampleDto(
    CollectingEventDto event) {
    MaterialSampleDto materialSampleDto = new MaterialSampleDto();
    materialSampleDto.setDwcCatalogNumber(dwcCatalogNumber);
    materialSampleDto.setCollectingEvent(event);
    materialSampleDto.setGroup(group);
    materialSampleDto.setUuid(UUID.randomUUID());
    materialSampleDto.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return materialSampleDto;
  }
}