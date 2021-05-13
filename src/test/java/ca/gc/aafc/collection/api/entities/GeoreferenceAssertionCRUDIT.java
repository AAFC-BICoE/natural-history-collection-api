package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GeoreferenceAssertionCRUDIT extends CollectionModuleBaseIT {

  private static final LocalDate testGeoreferencedDate = LocalDate.now();
  private static final List<UUID> agentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());
  public static final boolean PRIMARY = true;

  private CollectingEvent event;

  @BeforeEach
  void setUp() {
    event = CollectingEventFactory.newCollectingEvent()
      .uuid(UUID.randomUUID()).build();
    collectingEventService.create(event);
  }

  @Test
  public void create() {
    GeoreferenceAssertion geoReferenceAssertion = newAssertion();
    assertNull(geoReferenceAssertion.getId());
    service.save(geoReferenceAssertion);
    assertNotNull(geoReferenceAssertion.getId());
    assertEquals(agentIdentifiers, geoReferenceAssertion.getGeoreferencedBy());
  }

  @Test
  public void testFind() {
    GeoreferenceAssertion geoReferenceAssertion = newAssertion();
    service.save(geoReferenceAssertion);

    GeoreferenceAssertion fetchedGeoreferenceAssertion = service.find(
      GeoreferenceAssertion.class,
      geoReferenceAssertion.getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedGeoreferenceAssertion.getId());
    assertEquals(12.123456, fetchedGeoreferenceAssertion.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedGeoreferenceAssertion.getDwcDecimalLongitude());
    assertEquals(10, fetchedGeoreferenceAssertion.getDwcCoordinateUncertaintyInMeters());
    assertEquals(testGeoreferencedDate, fetchedGeoreferenceAssertion.getDwcGeoreferencedDate());
    assertEquals(PRIMARY, fetchedGeoreferenceAssertion.getIsPrimary());
    assertNotNull(fetchedGeoreferenceAssertion.getCreatedOn());
  }

  private GeoreferenceAssertion newAssertion() {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(12.123456)
      .dwcDecimalLongitude(45.01)
      .dwcCoordinateUncertaintyInMeters(10)
      .dwcGeoreferencedDate(testGeoreferencedDate)
      .isPrimary(PRIMARY)
      .collectingEvent(event)
      .georeferencedBy(agentIdentifiers)
      .build();
  }
}
