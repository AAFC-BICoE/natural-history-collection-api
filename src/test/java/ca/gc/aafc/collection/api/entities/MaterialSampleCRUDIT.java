package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;

@SpringBootTest(properties = "keycloak.enabled=true")

public class MaterialSampleCRUDIT extends CollectionModuleBaseIT {

    @Inject
    private PreparationTypeService preparationTypeService;

    @Inject
    private MaterialSampleService materialSampleService;

    private List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

    private static final String dwcCatalogNumber = "S-4313";
    private static final String expectedCreatedBy = "dina-save";
    private static final String sampleMaterialName = "lake water sample";

    private static final String preparationExpectedCreatedBy = "preparation-dina-save";
    private static final String preparationExpectedGroup = "dina-group-save";
    private static final String preparationExpectedName = "isolate lake water sample";

    private PreparationType preparationType;
    private MaterialSample materialSample;

    @BeforeEach
    void setup() {
        preparationType = PreparationTypeFactory.newPreparationType()
            .createdBy(preparationExpectedCreatedBy)
            .group(preparationExpectedGroup)
            .name(preparationExpectedName)
            .build();
        preparationTypeService.create(preparationType);

        materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName(sampleMaterialName)
            .preparationType(preparationType)
            .build();
        materialSampleService.create(materialSample);
    }

    @Test
    public void testCreate() {
        
        assertNotNull(preparationType.getId());
        assertNotNull(materialSample.getId());

        assertEquals(dwcCatalogNumber, materialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
        assertEquals(preparationType.getId(), materialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, materialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, materialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, materialSample.getPreparationType().getName());
    }

    @Test
    public void testFind() {

        MaterialSample fetchedMaterialSample = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);

        assertEquals(materialSample.getId(), fetchedMaterialSample.getId());
        assertEquals(dwcCatalogNumber, fetchedMaterialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, fetchedMaterialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, fetchedMaterialSample.getAttachment());
        assertEquals(sampleMaterialName, fetchedMaterialSample.getMaterialSampleName());
        assertEquals(preparationType.getId(), fetchedMaterialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, fetchedMaterialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, fetchedMaterialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, fetchedMaterialSample.getPreparationType().getName());    
    }

    @Test
    public void testParentChildRelationship() {
        MaterialSample child = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber("child-" + dwcCatalogNumber)
            .createdBy("child-" + expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName("child-" + sampleMaterialName)
            .preparationType(preparationType)
            .parentMaterialSample(materialSample)
            .build();

        materialSampleService.create(child);

        MaterialSample fetchedParent = materialSampleService.findOne(materialSample.getUuid(), MaterialSample.class);

        assertEquals(fetchedParent.getUuid(), child.getParentMaterialSample().getUuid());
        assertEquals(fetchedParent.getSubMaterialSamples().get(0).getUuid(), child.getUuid());

    }
    
}
