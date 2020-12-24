package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CollectorGroupCRUDIT extends CollectionModuleBaseIT {

  @Test
  public void testSave() {
    CollectorGroup collectorGroup = CollectorGroupFactory.newCollectorGroup()
        .build();
    assertNull(collectorGroup.getId());
    service.save(collectorGroup);
    assertNotNull(collectorGroup.getId());
  }

  @Test
  public void testFind() {
    UUID firstAgentIdentifier = UUID.randomUUID();
    UUID secondAgentIdentifier = UUID.randomUUID();
    LinkedHashSet<UUID> identifiers = new LinkedHashSet<UUID>();
    identifiers.add(firstAgentIdentifier);
    identifiers.add(secondAgentIdentifier);

    CollectorGroup collectorGroup = CollectorGroupFactory.newCollectorGroup()
        .name("test collector group")
        .agentIdentifiers(identifiers)
        .build();
    service.save(collectorGroup);

    CollectorGroup fetchedCollectorGroup = service.find(CollectorGroup.class, collectorGroup.getId());
    assertEquals(collectorGroup.getId(), fetchedCollectorGroup.getId());
    assertTrue(fetchedCollectorGroup.getAgentIdentifiers().contains(firstAgentIdentifier));
    assertTrue(fetchedCollectorGroup.getAgentIdentifiers().contains(secondAgentIdentifier));    
    assertEquals(collectorGroup.getName(), fetchedCollectorGroup.getName());
    assertNotNull(fetchedCollectorGroup.getCreatedOn());
  }

}
