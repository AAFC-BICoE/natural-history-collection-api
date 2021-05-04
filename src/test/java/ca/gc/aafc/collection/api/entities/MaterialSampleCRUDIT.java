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

        MaterialSample  materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName(sampleMaterialName)
            .build();
        assertNull(materialSample.getId());
        dbService.save(materialSample);
        assertNotNull(materialSample.getId());
        assertEquals(dwcCatalogNumber, materialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
    }

    @Test
    public void testFind() {
        String dwcCatalogNumber = "F-4313";
        String expectedCreatedBy = "dina-find";
        String sampleMaterialName = "lake water sample";

        MaterialSample materialSample = MaterialSampleFactory.newMaterialSample()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .materialSampleName(sampleMaterialName)
            .build();
        dbService.save(materialSample);

        MaterialSample fetchedMaterialSample = dbService.find(MaterialSample.class, materialSample.getId());
        assertEquals(materialSample.getId(), fetchedMaterialSample.getId());
        assertEquals(dwcCatalogNumber, fetchedMaterialSample.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, materialSample.getCreatedBy());
        assertEquals(attachmentIdentifiers, materialSample.getAttachment());
        assertEquals(sampleMaterialName, materialSample.getMaterialSampleName());
        assertNotNull(fetchedMaterialSample.getCreatedOn());
    }
    
}
