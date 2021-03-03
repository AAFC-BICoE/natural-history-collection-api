package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

public class GeoreferenceAssertionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private static final OffsetDateTime testGeoreferencedDate = OffsetDateTime.now();


  @Test
  public void testSave() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
        .build();
    assertNull(geoReferenceAssertion.getId());
    dbService.save(geoReferenceAssertion);
    assertNotNull(geoReferenceAssertion.getId());
  }

  @Test
  public void testFind() {
    GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
        .dwcDecimalLatitude(12.123456)
        .dwcDecimalLongitude(45.01)
        .dwcCoordinateUncertaintyInMeters(10)
        .dwcGeoreferencedDate(testGeoreferencedDate)
        .build();
    dbService.save(geoReferenceAssertion);

    GeoreferenceAssertion fetchedGeoreferenceAssertion = dbService.find(GeoreferenceAssertion.class, geoReferenceAssertion.getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedGeoreferenceAssertion.getId());
    assertEquals(12.123456, fetchedGeoreferenceAssertion.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedGeoreferenceAssertion.getDwcDecimalLongitude());
    assertEquals(10, fetchedGeoreferenceAssertion.getDwcCoordinateUncertaintyInMeters());
    assertEquals(testGeoreferencedDate, fetchedGeoreferenceAssertion.getDwcGeoreferencedDate());
    assertNotNull(fetchedGeoreferenceAssertion.getCreatedOn());
  }
}
