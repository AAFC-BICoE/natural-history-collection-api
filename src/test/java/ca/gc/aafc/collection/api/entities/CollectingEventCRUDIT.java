package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoReferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private GeoReferenceAssertion geoReferenceAssertion = GeoReferenceAssertionFactory.newGeoReferenceAssertion()
    .dwcDecimalLatitude(12.123456)
    .dwcDecimalLongitude(45.01)
    .build();

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality  = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcGeoreferenceSources  = "https://www.geonames.org/" ;
  private static final OffsetDateTime dwcGeoreferencedDate = OffsetDateTime.now();  
  private static final String dwcVerbatimLatitude = "latitude 12.123456";
  private static final String dwcVerbatimLongitude = "long 45.01";
  private static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  private static final String dwcVerbatimSRS = "EPSG:4326";
  private static final String dwcVerbatimElevation = "100-200 m";
  private static final String dwcVerbatimDepth = "10-20 m ";  
  private static final String[] dwcRecordNumbers = new String[] { "80-79", "80-80"};  

  @Test
  public void testSave() {
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
       .build();
    dbService.save(geoReferenceAssertion,false);
    collectingEvent.setGeoReferenceAssertions(Collections.singletonList(geoReferenceAssertion));
    assertNull(collectingEvent.getId());
    dbService.save(collectingEvent, false);
    assertNotNull(collectingEvent.getId());
  }

  @Test
  public void testFind() {
    LocalDateTime testDateTime = LocalDateTime.of(2000,2,3,0,0);
    dbService.save(geoReferenceAssertion,false);
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
        .geoReferenceAssertions(Collections.singletonList((geoReferenceAssertion)))        
        .startEventDateTime(testDateTime)
        .startEventDateTimePrecision((byte) 8)
        .dwcRecordedBy(dwcRecordedBy)
        .dwcVerbatimLocality(dwcVerbatimLocality)
        .dwcGeoreferencedDate(dwcGeoreferencedDate)
        .dwcGeoreferenceSources(dwcGeoreferenceSources)
        .dwcVerbatimLatitude(dwcVerbatimLatitude)
        .dwcVerbatimLongitude(dwcVerbatimLongitude)
        .dwcVerbatimCoordinateSystem(dwcVerbatimCoordinateSystem)
        .dwcVerbatimSRS(dwcVerbatimSRS)
        .dwcVerbatimElevation(dwcVerbatimElevation)
        .dwcVerbatimDepth(dwcVerbatimDepth)
        .dwcRecordNumbers(dwcRecordNumbers)
        .build();
    dbService.save(collectingEvent,false);

    CollectingEvent fetchedCollectingEvent = dbService
        .find(CollectingEvent.class, collectingEvent.getId());
    assertEquals(collectingEvent.getId(), fetchedCollectingEvent.getId());
    assertEquals(testDateTime, fetchedCollectingEvent.getStartEventDateTime());
    assertEquals((byte) 8, fetchedCollectingEvent.getStartEventDateTimePrecision());
    assertEquals(dwcRecordedBy, fetchedCollectingEvent.getDwcRecordedBy());
    assertEquals(
      geoReferenceAssertion.getId(),
      fetchedCollectingEvent.getGeoReferenceAssertions().iterator().next().getId());    
    assertEquals(
      12.123456,
      fetchedCollectingEvent.getGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());    
    assertNotNull(fetchedCollectingEvent.getCreatedOn());
    assertEquals(dwcVerbatimLocality, fetchedCollectingEvent.getDwcVerbatimLocality());
    assertEquals(dwcGeoreferenceSources, fetchedCollectingEvent.getDwcGeoreferenceSources());
    assertEquals(dwcGeoreferencedDate, fetchedCollectingEvent.getDwcGeoreferencedDate());
    assertEquals(dwcVerbatimLatitude, fetchedCollectingEvent.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, fetchedCollectingEvent.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem,
        fetchedCollectingEvent.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, fetchedCollectingEvent.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, fetchedCollectingEvent.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, fetchedCollectingEvent.getDwcVerbatimDepth());
    assertArrayEquals(dwcRecordNumbers, fetchedCollectingEvent.getDwcRecordNumbers());
  }

}
