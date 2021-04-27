package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.PhysicalEntityDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessDefinitionDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.crnk.core.queryspec.QuerySpec;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationProcessRepositoryIT extends CollectionModuleBaseIT {

  @Inject 
  private PreparationProcessRepository preparationProcessRepository;

  private static final String dwcCatalogNumber = "R-4313";
  private static final String group = "aafc";
  private static final String name = "preparation process definition";
  private static final LocalDateTime startDateTime= LocalDateTime.of(2007, 12, 3, 10, 15, 30);
  private static final LocalDateTime endDateTime= LocalDateTime.of(2019, 12, 3, 10, 15, 30);
  
  @Test
  @WithMockKeycloakUser(username = "test user")
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PhysicalEntityDto pe = newPhysicalEntityDto(null);
    PreparationProcessDefinitionDto ppd = newPreparationProcessDefinitionDto();
    PreparationProcessDto pp = newPreparationProcessDto(pe, ppd);
    PreparationProcessDto result = preparationProcessRepository.findOne(
      preparationProcessRepository.create(pp).getUuid(),
      new QuerySpec(PreparationProcessDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(pp.getAgentId(), result.getAgentId());
    assertEquals(pp.getStartDateTime(), result.getStartDateTime());
    assertEquals(pp.getEndDateTime(), result.getEndDateTime());
    assertEquals(pp.getPreparationProcessDefinition().getUuid(), result.getPreparationProcessDefinition().getUuid());
    assertEquals(pp.getSourcePhysicalEntity().getUuid(), result.getSourcePhysicalEntity().getUuid());
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
    return ppd;
  }
  
  private PhysicalEntityDto newPhysicalEntityDto(
    CollectingEventDto event) {
    PhysicalEntityDto pe = new PhysicalEntityDto();
    pe.setDwcCatalogNumber(dwcCatalogNumber);
    pe.setCollectingEvent(event);
    pe.setGroup(group);
    pe.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
    return pe;
  }

  private CollectingEventDto newEventDto() {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup(group);
    ce.setStartEventDateTime(ISODateTime.parse("2020").toString());
    ce.setCreatedBy("dina");
    return ce;
  }
  
}
