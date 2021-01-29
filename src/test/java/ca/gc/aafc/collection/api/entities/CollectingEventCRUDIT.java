package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality  = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcGeoreferenceSources  = "https://www.geonames.org/" ;
  private static final OffsetDateTime dwcGeoreferencedDate = OffsetDateTime.now();  

  @Test
  public void testSave() {
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
        .build();
    assertNull(collectingEvent.getId());
    dbService.save(collectingEvent);
    assertNotNull(collectingEvent.getId());
  }

  @Test
  public void testFind() {
    LocalDateTime testDateTime = LocalDateTime.of(2000,2,3,0,0);
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
        .dwcDecimalLatitude(12.123456)
        .dwcDecimalLongitude(45.01)
        .startEventDateTime(testDateTime)
        .startEventDateTimePrecision((byte) 8)
        .dwcRecordedBy(dwcRecordedBy)
        .dwcVerbatimLocality(dwcVerbatimLocality)
        .dwcGeoreferencedDate(dwcGeoreferencedDate)
        .dwcGeoreferenceSources(dwcGeoreferenceSources)
        .build();
    dbService.save(collectingEvent);

    CollectingEvent fetchedCollectingEvent = dbService.find(CollectingEvent.class, collectingEvent.getId());
    assertEquals(collectingEvent.getId(), fetchedCollectingEvent.getId());
    assertEquals(12.123456, fetchedCollectingEvent.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedCollectingEvent.getDwcDecimalLongitude());
    assertEquals(testDateTime, fetchedCollectingEvent.getStartEventDateTime());
    assertEquals((byte) 8, fetchedCollectingEvent.getStartEventDateTimePrecision());
    assertEquals(dwcRecordedBy, fetchedCollectingEvent.getDwcRecordedBy());
    assertNotNull(fetchedCollectingEvent.getCreatedOn());
    assertEquals(dwcVerbatimLocality, fetchedCollectingEvent.getDwcVerbatimLocality());
    assertEquals(dwcGeoreferenceSources, fetchedCollectingEvent.getDwcGeoreferenceSources());
    assertEquals(dwcGeoreferencedDate, fetchedCollectingEvent.getDwcGeoreferencedDate());
  }

}
