package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.PhysicalEntityDto;
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
  private PhysicalEntityRepository physicalEntityRepository;

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
    PhysicalEntityDto pe = physicalEntityRepository.findOne(
      physicalEntityRepository.create(newPhysicalEntityDto()).getUuid(), 
      new QuerySpec(PhysicalEntityDto.class));
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
    assertEquals(ppe.getPhysicalEntity().getUuid(), result.getPhysicalEntity().getUuid());
  }

  private PreparationProcessElementDto newPreparationProcessElementDto(
    PreparationProcessDto pp,
    PhysicalEntityDto pe) {
      PreparationProcessElementDto ppe = new PreparationProcessElementDto();
      ppe.setPhysicalEntity(pe);
      ppe.setPreparationProcess(pp);
      ppe.setUuid(UUID.randomUUID());
      return ppe;
  }

  private PreparationProcessDto newPreparationProcessDto(
    PhysicalEntityDto pe,
    PreparationProcessDefinitionDto ppd) {
    PreparationProcessDto pp = new PreparationProcessDto();
    pp.setStartDateTime(startDateTime);
    pp.setEndDateTime(endDateTime);
    pp.setAgentId(UUID.randomUUID());
    pp.setSourcePhysicalEntity(pe);
    pp.setPreparationProcessDefinition(ppd);
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
  
  private PhysicalEntityDto newPhysicalEntityDto() {
    PhysicalEntityDto pe = new PhysicalEntityDto();
    pe.setDwcCatalogNumber(dwcCatalogNumber);
    pe.setGroup(group);
    pe.setUuid(UUID.randomUUID());
    pe.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return pe;
  }
}
