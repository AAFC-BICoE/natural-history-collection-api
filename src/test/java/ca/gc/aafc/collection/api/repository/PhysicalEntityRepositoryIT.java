package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.PhysicalEntityDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")

public class PhysicalEntityRepositoryIT extends CollectionModuleBaseIT {

    @Inject
    private PhysicalEntityRepository physicalEntityRepository;

    @Inject
    private CollectingEventRepository eventRepository;

    private static final String dwcCatalogNumber = "R-4313";

    @Test
    @WithMockKeycloakUser(username = "test user")
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
        PhysicalEntityDto pe = newPhysicalEntity(dwcCatalogNumber, null);
        PhysicalEntityDto result = physicalEntityRepository.findOne(physicalEntityRepository.create(pe).getUuid(), 
                new QuerySpec(PhysicalEntityDto.class));
        assertNotNull(result.getCreatedBy());
        assertEquals(pe.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
        assertEquals(dwcCatalogNumber, result.getDwcCatalogNumber());
        }

    @Test
    @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
    public void create_recordCreated() {
        CollectingEventDto event = eventRepository.findOne(
            eventRepository.create(newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));
        PhysicalEntityDto result = physicalEntityRepository.findOne(
            physicalEntityRepository.create(newPhysicalEntity(dwcCatalogNumber, event)).getUuid(),
            new QuerySpec(PhysicalEntityDto.class)
            );
        assertEquals(dwcCatalogNumber, result.getDwcCatalogNumber());
        assertEquals(event.getUuid(), result.getCollectingEvent().getUuid());
        }

    private PhysicalEntityDto newPhysicalEntity(
        String dwcCatalogNumber, 
        CollectingEventDto event) {
        PhysicalEntityDto pe = new PhysicalEntityDto();
        pe.setDwcCatalogNumber(dwcCatalogNumber);
        pe.setCollectingEvent(event);
        pe.setAttachment(List.of(
            ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));
        return pe;
    }

    private CollectingEventDto newEventDto() {
        CollectingEventDto ce = new CollectingEventDto();
        ce.setGroup("aafc");
        ce.setStartEventDateTime(ISODateTime.parse("2020").toString());
        ce.setCreatedBy("dina");
        return ce;
    }
    
}
