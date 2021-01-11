package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private CollectorGroup createCollectorGroup() {
    CollectorGroup group = CollectorGroupFactory.newCollectorGroup().build();
    service.save(group);
    return group;
  }

  @Test
  public void testSave() {
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .collectorGroup(createCollectorGroup())
      .build();
    assertNull(collectingEvent.getId());
    dbService.save(collectingEvent);
    assertNotNull(collectingEvent.getId());
  }

  @Test
  public void testFind() {
    LocalDateTime testDateTime = LocalDateTime.of(2000, 2, 3, 0, 0);
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
      .decimalLatitude(12.123456)
      .decimalLongitude(45.01)
      .collectorGroup(createCollectorGroup())
      .startEventDateTime(testDateTime)
      .startEventDateTimePrecision((byte) 8)
      .build();
    dbService.save(collectingEvent);

    CollectingEvent fetchedCollectingEvent = dbService.find(CollectingEvent.class, collectingEvent.getId());
    assertEquals(collectingEvent.getId(), fetchedCollectingEvent.getId());
    assertEquals(12.123456, fetchedCollectingEvent.getDecimalLatitude());
    assertEquals(45.01, fetchedCollectingEvent.getDecimalLongitude());
    assertEquals(testDateTime, fetchedCollectingEvent.getStartEventDateTime());
    assertEquals((byte) 8, fetchedCollectingEvent.getStartEventDateTimePrecision());
    assertNotNull(fetchedCollectingEvent.getCreatedOn());

  }

}
