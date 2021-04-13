package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;
import ca.gc.aafc.collection.api.service.GeoReferenceAssertionService;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.crnk.core.queryspec.QuerySpec;


import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class GeoreferenceAssertionIT extends CollectionModuleBaseIT{
  
  @Inject
  private GeoReferenceAssertionService georeferenceAssertionService;

  private final static double latitude = 12.34;
  private final static double longitude = 56.78;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcVerbatimLatitude = "latitude 12.123456";
  private static final String dwcVerbatimLongitude = "long 45.01";
  private static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  private static final String dwcVerbatimSRS = "EPSG:4326";
  private static final String dwcVerbatimElevation = "100-200 m";
  private static final String dwcVerbatimDepth = "10-20 m ";
  private static final LocalDate testGeoreferencedDate = LocalDate.now();

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void createUnlinkedGeoreference_success() {
    GeoreferenceAssertion ga = newGeoreferenceAssertion();
    georeferenceAssertionService.create(ga);
    assertNotNull(ga.getUuid());
    assertEquals(latitude, ga.getDwcDecimalLatitude());
    assertEquals(longitude, ga.getDwcDecimalLongitude());
  }

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void linkedCollectingEventHasNoPrimaryAssertion_throwsIllegalArgumentException() {
    GeoreferenceAssertion ga = newGeoreferenceAssertion();
    CollectingEvent ce = newEvent();
    ga.setCollectingEvent(ce);
    assertNull(ga.getCollectingEvent().getPrimaryGeoreferenceAssertion());
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,  () -> {
        georeferenceAssertionService.create(ga);
      });
    
    String expectedMessage = "Linked Collecting Event must have a Primary Georeference Assertion.";
    String actualMessage = exception.getMessage();
  
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void georeferenceVerificationStatusEqualsGeoreferencingNotPossible_throwsIllegalArgumentException() {
    GeoreferenceAssertion ga = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .dwcDecimalLongitude(longitude)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .dwcGeoreferenceVerificationStatus(GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE)
      .build();

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,  () -> {
        georeferenceAssertionService.create(ga);
      });
    
      String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
      String actualMessage = exception.getMessage();
    
      assertTrue(actualMessage.contains(expectedMessage));
    
  }

  private GeoreferenceAssertion newGeoreferenceAssertion() {
    GeoreferenceAssertion ga = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .dwcDecimalLongitude(longitude)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .build();
    return ga;
  }
  
  private CollectingEvent newEvent() {
    CollectingEvent ce = CollectingEventFactory.newCollectingEvent()
      .startEventDateTime(LocalDateTime.of(startDate, startTime))
      .startEventDateTimePrecision((byte) 8)
      .endEventDateTime(LocalDateTime.of(endDate, endTime))
      .endEventDateTimePrecision((byte) 8)
      .verbatimEventDateTime("XI-02-1798")
      .dwcVerbatimCoordinates("26.089, 106.36")
      .attachment(List.of(UUID.randomUUID()))
      .collectors(List.of(UUID.randomUUID()))
      .dwcRecordedBy(dwcRecordedBy)
      .dwcVerbatimLocality(dwcVerbatimLocality)
      .dwcVerbatimLatitude(dwcVerbatimLatitude)
      .dwcVerbatimLongitude(dwcVerbatimLongitude)
      .dwcVerbatimCoordinateSystem(dwcVerbatimCoordinateSystem)
      .dwcVerbatimSRS(dwcVerbatimSRS)
      .dwcVerbatimElevation(dwcVerbatimElevation)
      .dwcVerbatimDepth(dwcVerbatimDepth)   
      .build();
    return ce;
  }

}
