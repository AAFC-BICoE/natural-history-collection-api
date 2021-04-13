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
import ca.gc.aafc.collection.api.testsupport.factories.PhysicalEntityFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

@SpringBootTest(properties = "keycloak.enabled=true")

public class PhysicalEntityCRUDIT extends CollectionModuleBaseIT {

    @Inject
    private DatabaseSupportService dbService;

    private List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

    @Test
    public void testSave() {
        String dwcCatalogNumber = "S-4313";
        String expectedCreatedBy = "dina-save";

        PhysicalEntity  physicalEntity = PhysicalEntityFactory.newPhysicalEntity()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .build();
        assertNull(physicalEntity.getId());
        dbService.save(physicalEntity);
        assertNotNull(physicalEntity.getId());
        assertEquals(dwcCatalogNumber, physicalEntity.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, physicalEntity.getCreatedBy());
        assertEquals(attachmentIdentifiers, physicalEntity.getAttachment());
    }

    @Test
    public void testFind() {
        String dwcCatalogNumber = "F-4313";
        String expectedCreatedBy = "dina-find";

        PhysicalEntity physicalEntity = PhysicalEntityFactory.newPhysicalEntity()
            .dwcCatalogNumber(dwcCatalogNumber)
            .createdBy(expectedCreatedBy)
            .attachment(attachmentIdentifiers)
            .build();
        dbService.save(physicalEntity);

        PhysicalEntity fetchedPhysicalEntity = dbService.find(PhysicalEntity.class, physicalEntity.getId());
        assertEquals(physicalEntity.getId(), fetchedPhysicalEntity.getId());
        assertEquals(dwcCatalogNumber, fetchedPhysicalEntity.getDwcCatalogNumber());
        assertEquals(expectedCreatedBy, physicalEntity.getCreatedBy());
        assertEquals(attachmentIdentifiers, physicalEntity.getAttachment());
        assertNotNull(fetchedPhysicalEntity.getCreatedOn());
    }
    
}
