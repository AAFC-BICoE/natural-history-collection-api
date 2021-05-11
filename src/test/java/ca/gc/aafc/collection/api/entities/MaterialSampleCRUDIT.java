package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

@SpringBootTest(properties = "keycloak.enabled=true")

public class MaterialSampleCRUDIT extends CollectionModuleBaseIT {

    @Inject
    private DatabaseSupportService dbService;

    private List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

    @Test
    public void testSave() {
        String dwcCatalogNumber = "S-4313";
        String expectedCreatedBy = "dina-save";
        String sampleMaterialName = "lake water sample";

        String preparationExpectedCreatedBy = "preparation-dina-save";
        String preparationExpectedGroup = "dina";
        String preparationExpectedName = "isolate";

        PreparationType preparationType = PreparationTypeFactory.newPreparationType()
            .createdBy(preparationExpectedCreatedBy)
            .group(preparationExpectedGroup)
            .name(preparationExpectedName)
            .build();
        dbService.save(preparationType);

        MaterialSample  materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName(sampleMaterialName)
            .preparationType(preparationType)
            .build();
        assertNull(materialSample.getId());
        dbService.save(materialSample);
        assertNotNull(materialSample.getId());
        assertEquals(dwcCatalogNumber, materialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
        assertNotNull(preparationType.getId());
        assertEquals(preparationType.getId(), materialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, materialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, materialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, materialSample.getPreparationType().getName());
    }

    @Test
    public void testFind() {
        String dwcCatalogNumber = "F-4313";
        String expectedCreatedBy = "dina-find";
        String sampleMaterialName = "lake water sample";

        String preparationExpectedCreatedBy = "preparation-dina-find";
        String preparationExpectedGroup = "dina";
        String preparationExpectedName = "isolate";

        PreparationType preparationType = PreparationTypeFactory.newPreparationType()
            .createdBy(preparationExpectedCreatedBy)
            .group(preparationExpectedGroup)
            .name(preparationExpectedName)
            .build();
        dbService.save(preparationType);

        MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName(sampleMaterialName)
            .preparationType(preparationType)
            .build();
        dbService.save(materialSample);

        MaterialSample fetchedMaterialSample = dbService.find(MaterialSample.class, materialSample.getId());
        assertEquals(materialSample.getId(), fetchedMaterialSample.getId());
        assertEquals(dwcCatalogNumber, fetchedMaterialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
        assertNotNull(fetchedMaterialSample.getCreatedOn());
        assertNotNull(preparationType.getId());
        assertEquals(preparationType.getId(), materialSample.getPreparationType().getId());
        assertEquals(preparationExpectedCreatedBy, materialSample.getPreparationType().getCreatedBy());
        assertEquals(preparationExpectedGroup, materialSample.getPreparationType().getGroup());
        assertEquals(preparationExpectedName, materialSample.getPreparationType().getName());    }
    
}
