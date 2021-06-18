package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled=true")

public class MaterialSampleRepositoryIT extends CollectionModuleBaseIT {

    @Inject
    private MaterialSampleRepository materialSampleRepository;

    @Inject
    private CollectingEventRepository eventRepository;

    @Test
    @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
        MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
        MaterialSampleDto result = materialSampleRepository.findOne(materialSampleRepository.create(materialSampleDto).getUuid(), 
                new QuerySpec(MaterialSampleDto.class));
        assertNotNull(result.getCreatedBy());
        assertEquals(materialSampleDto.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
        assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
        assertEquals(MaterialSampleTestFixture.DWC_OTHER_CATALOG_NUMBERS, result.getDwcOtherCatalogNumbers());
        assertEquals(MaterialSampleTestFixture.GROUP, result.getGroup());
        assertEquals(MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME, result.getMaterialSampleName());
        assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().getId());
        assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    }

    @Test
    @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
    public void create_recordCreated() {
        CollectingEventDto event = eventRepository.findOne(
            eventRepository.create(CollectingEventTestFixture.newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));
        MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
        materialSampleDto.setCollectingEvent(event);
        MaterialSampleDto result = materialSampleRepository.findOne(
            materialSampleRepository.create(materialSampleDto).getUuid(),
            new QuerySpec(MaterialSampleDto.class)
            );
        assertEquals(MaterialSampleTestFixture.DWC_CATALOG_NUMBER, result.getDwcCatalogNumber());
        assertEquals(event.getUuid(), result.getCollectingEvent().getUuid());
        assertEquals(MaterialSampleTestFixture.PREPARED_BY.toString(), result.getPreparedBy().getId());
        assertEquals(MaterialSampleTestFixture.PREPARATION_DATE, result.getPreparationDate());
    }

    @Test
    @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
    public void updateFromDifferentGroup_throwAccessDenied() {
        MaterialSample testMaterialSample = MaterialSampleFactory.newMaterialSample()
            .group(MaterialSampleTestFixture.GROUP)
            .createdBy("dina")
            .build();
        materialSampleService.create(testMaterialSample);
        MaterialSampleDto retrievedMaterialSample = materialSampleRepository.findOne(testMaterialSample.getUuid(),
            new QuerySpec(MaterialSampleDto.class));
        assertThrows(AccessDeniedException.class, () -> materialSampleRepository.save(retrievedMaterialSample));
    }
    
}
