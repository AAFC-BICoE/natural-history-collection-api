package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;
import ca.gc.aafc.collection.api.service.GeoReferenceAssertionService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GeoreferenceAssertionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  @Inject
  private GeoReferenceAssertionService service;

  private static final LocalDate testGeoreferencedDate = LocalDate.now();
  private static final GeoreferenceVerificationStatus georeferenceVerificationStatus = GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE;
  private static final List<UUID> agentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

  private CollectingEvent event;

  @BeforeEach
  void setUp() {
    event = CollectingEventFactory.newCollectingEvent()
      .uuid(UUID.randomUUID()).build();
    dbService.save(event);
  }

  @Test
  public void create() {
    GeoreferenceAssertion geoReferenceAssertion = newAssertion();
    assertNull(geoReferenceAssertion.getId());
    service.create(geoReferenceAssertion);
    assertNotNull(geoReferenceAssertion.getId());
    assertEquals(agentIdentifiers, geoReferenceAssertion.getGeoreferencedBy());
  }

  @Test
  public void testFind() {
    GeoreferenceAssertion geoReferenceAssertion = newAssertion();
    service.create(geoReferenceAssertion);

    GeoreferenceAssertion fetchedGeoreferenceAssertion = dbService.find(
      GeoreferenceAssertion.class,
      geoReferenceAssertion.getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedGeoreferenceAssertion.getId());
    assertEquals(12.123456, fetchedGeoreferenceAssertion.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedGeoreferenceAssertion.getDwcDecimalLongitude());
    assertEquals(10, fetchedGeoreferenceAssertion.getDwcCoordinateUncertaintyInMeters());
    assertEquals(testGeoreferencedDate, fetchedGeoreferenceAssertion.getDwcGeoreferencedDate());
    assertNotNull(fetchedGeoreferenceAssertion.getCreatedOn());
  }

  private GeoreferenceAssertion newAssertion() {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(12.123456)
      .dwcDecimalLongitude(45.01)
      .dwcCoordinateUncertaintyInMeters(10)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .collectingEvent(event)
      .georeferencedBy(agentIdentifiers)
      .build();
  }
}
