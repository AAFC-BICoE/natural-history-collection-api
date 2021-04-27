package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.PhysicalEntityDto;
import ca.gc.aafc.collection.api.dto.PreparationProcessDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class PreparationProcessRepositoryIT extends CollectionModuleBaseIT {

  // @Inject 
  // private PreparationProcessRepository preparationProcessRepository

  // For Physical Entity
  private static final String dwcCatalogNumber = "R-4313";
  private static final String group = "aafc";
  
  private PhysicalEntityDto newPhysicalEntityDto(
    String dwcCatalogNumber, 
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
