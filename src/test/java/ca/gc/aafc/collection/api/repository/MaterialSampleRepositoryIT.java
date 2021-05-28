package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")

public class MaterialSampleRepositoryIT extends CollectionModuleBaseIT {

    @Inject
    private MaterialSampleRepository materialSampleRepository;

    @Inject
    private MaterialSampleService materialSampleService;

    @Inject
    private CollectingEventRepository eventRepository;

    private static final String dwcCatalogNumber = "R-4313";
    private static final String[] dwcOtherCatalogNumbers= new String[]{"A-1111", "B-2222"};
    private static final String group = "aafc";
    private static final String materialSampleName = "ocean water sample";
    private static final UUID preparedBy = UUID.randomUUID();
    private static final LocalDate preparationDate = LocalDate.now();


    @Test
    @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
        MaterialSampleDto pe = newMaterialSample(dwcCatalogNumber, null);
        MaterialSampleDto result = materialSampleRepository.findOne(materialSampleRepository.create(pe).getUuid(), 
                new QuerySpec(MaterialSampleDto.class));
        assertNotNull(result.getCreatedBy());
        assertEquals(pe.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
        assertEquals(dwcCatalogNumber, result.getDwcCatalogNumber());
        assertEquals(dwcOtherCatalogNumbers, result.getDwcOtherCatalogNumbers());
        assertEquals(group, result.getGroup());
        assertEquals(materialSampleName, result.getMaterialSampleName());
        assertEquals(preparedBy.toString(),result.getPreparedBy().getId());
        assertEquals(preparationDate, result.getPreparationDate());
    }

    @Test
    @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
    public void create_recordCreated() {
        CollectingEventDto event = eventRepository.findOne(
            eventRepository.create(newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));
        MaterialSampleDto result = materialSampleRepository.findOne(
            materialSampleRepository.create(newMaterialSample(dwcCatalogNumber, event)).getUuid(),
            new QuerySpec(MaterialSampleDto.class)
            );
        assertEquals(dwcCatalogNumber, result.getDwcCatalogNumber());
        assertEquals(event.getUuid(), result.getCollectingEvent().getUuid());
        assertEquals(preparedBy.toString(), result.getPreparedBy().getId());
        assertEquals(preparationDate, result.getPreparationDate());
    }

    @Test
    @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
    public void updateFromDifferentGroup_throwAccessDenied() {
        MaterialSample testMaterialSample = MaterialSampleFactory.newMaterialSample()
            .group(group)
            .createdBy("dina")
            .build();
        materialSampleService.create(testMaterialSample);
        MaterialSampleDto retrievedMaterialSample = materialSampleRepository.findOne(testMaterialSample.getUuid(),
            new QuerySpec(MaterialSampleDto.class));
        assertThrows(AccessDeniedException.class, () -> materialSampleRepository.save(retrievedMaterialSample));
    }


    private MaterialSampleDto newMaterialSample(
        String dwcCatalogNumber, 
        CollectingEventDto event) {
        MaterialSampleDto pe = new MaterialSampleDto();
        pe.setDwcCatalogNumber(dwcCatalogNumber);
        pe.setDwcOtherCatalogNumbers(dwcOtherCatalogNumbers);
        pe.setCollectingEvent(event);
        pe.setPreparedBy(ExternalRelationDto.builder().id(preparedBy.toString()).type("agent").build());
        pe.setPreparationDate(preparationDate);
        pe.setGroup(group);
        pe.setMaterialSampleName(materialSampleName);
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
