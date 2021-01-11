package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CollectorGroupCRUDIT extends CollectionModuleBaseIT {

  private List<UUID> identifiers = new ArrayList<UUID>();
  private UUID firstAgentIdentifier = UUID.randomUUID();
  private UUID secondAgentIdentifier = UUID.randomUUID();      
  private final String TEST_COLLECTOR_GROUP = "test collector group";

  @BeforeEach
  void setup(){
    identifiers.clear();
    identifiers.add(firstAgentIdentifier);
    identifiers.add(secondAgentIdentifier);
  }

  @Test
  public void testSave() {
    CollectorGroup collectorGroup = CollectorGroupFactory.newCollectorGroup()
       .agentIdentifiers(identifiers.toArray( new UUID[identifiers.size()]))
       .name(TEST_COLLECTOR_GROUP)
       .build();
    assertNull(collectorGroup.getId());
    service.save(collectorGroup);
    assertNotNull(collectorGroup.getId());
  }

  @Test
  public void testFind() {
    CollectorGroup collectorGroup = CollectorGroupFactory.newCollectorGroup()
        .name(TEST_COLLECTOR_GROUP)
        .agentIdentifiers(identifiers.toArray( new UUID[identifiers.size()]))
        .build();
    service.save(collectorGroup);

    CollectorGroup fetchedCollectorGroup = service.find(CollectorGroup.class, collectorGroup.getId());
    assertEquals(collectorGroup.getId(), fetchedCollectorGroup.getId());
    assertEquals(2, fetchedCollectorGroup.getAgentIdentifiers().length);
    assertEquals(collectorGroup.getAgentIdentifiers()[0], fetchedCollectorGroup.getAgentIdentifiers()[0]);
    assertEquals(collectorGroup.getName(), fetchedCollectorGroup.getName());
    assertNotNull(fetchedCollectorGroup.getCreatedOn());
  }
}
