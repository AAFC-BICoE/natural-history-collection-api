package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessDefinitionDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessElementDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import io.crnk.core.queryspec.QuerySpec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationProcessElementRepositoryIT extends CollectionModuleBaseIT {

  @Inject 
  private PreparationProcessElementRepository preparationProcessElementRepository;

  @Inject 
  private PreparationProcessRepository preparationProcessRepository;

  @Inject
  private MaterialSampleRepository physicalEntityRepository;

  @Inject 
  private PreparationProcessDefinitionRepository preparationProcessDefinitionRepository;
  
  private static final String dwcCatalogNumber = "R-4313";
  private static final String group = "aafc";
  private static final String name = "preparation process definition";
  private static final LocalDateTime startDateTime= LocalDateTime.of(2007, 12, 3, 10, 15, 30);
  private static final LocalDateTime endDateTime= LocalDateTime.of(2019, 12, 3, 10, 15, 30);

  @Test
  @WithMockKeycloakUser(username = "test user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    MaterialSampleDto pe = physicalEntityRepository.findOne(
      physicalEntityRepository.create(newMaterialSampleDto()).getUuid(), 
      new QuerySpec(MaterialSampleDto.class));
    PreparationProcessDefinitionDto ppd = preparationProcessDefinitionRepository.findOne(
      preparationProcessDefinitionRepository.create(newPreparationProcessDefinitionDto()).getUuid(),
      new QuerySpec(PreparationProcessDefinitionDto.class));
    PreparationProcessDto pp = preparationProcessRepository.findOne(
      preparationProcessRepository.create(newPreparationProcessDto(pe, ppd)).getUuid(),
      new QuerySpec(PreparationProcessDto.class));
    PreparationProcessElementDto ppe = newPreparationProcessElementDto(pp, pe);
    PreparationProcessElementDto result = preparationProcessElementRepository.findOne(
      preparationProcessElementRepository.create(ppe).getUuid(),
      new QuerySpec(PreparationProcessDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(ppe.getPreparationProcess().getUuid(), result.getPreparationProcess().getUuid());
    assertEquals(ppe.getMaterialSample().getUuid(), result.getMaterialSample().getUuid());
  }

  private PreparationProcessElementDto newPreparationProcessElementDto(
    PreparationProcessDto pp,
    MaterialSampleDto pe) {
      PreparationProcessElementDto ppe = new PreparationProcessElementDto();
      ppe.setMaterialSample(pe);
      ppe.setPreparationProcess(pp);
      ppe.setUuid(UUID.randomUUID());
      return ppe;
  }

  private PreparationProcessDto newPreparationProcessDto(
    MaterialSampleDto pe,
    PreparationProcessDefinitionDto ppd) {
    PreparationProcessDto pp = new PreparationProcessDto();
    pp.setStartDateTime(startDateTime);
    pp.setEndDateTime(endDateTime);
    pp.setAgentId(UUID.randomUUID());
    pp.setSourceMaterialSample(pe);
    pp.setPreparationProcessDefinition(ppd);
    pp.setUuid(UUID.randomUUID());
    return pp;
  }

  private PreparationProcessDefinitionDto newPreparationProcessDefinitionDto() {
    PreparationProcessDefinitionDto ppd = new PreparationProcessDefinitionDto();
    ppd.setName(name);
    ppd.setGroup(group);
    ppd.setUuid(UUID.randomUUID());
    ppd.setCreatedBy("test user");
    return ppd;
  }
  
  private MaterialSampleDto newMaterialSampleDto() {
    MaterialSampleDto pe = new MaterialSampleDto();
    pe.setDwcCatalogNumber(dwcCatalogNumber);
    pe.setGroup(group);
    pe.setUuid(UUID.randomUUID());
    pe.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return pe;
  }
}
