package ca.gc.aafc.collection.api.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.GeoReferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;

public class GeoReferenceAssertionCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;


  @Test
  public void testSave() {
    GeoReferenceAssertion geoReferenceAssertion = GeoReferenceAssertionFactory.newGeoReferenceAssertion()
        .build();
    assertNull(geoReferenceAssertion.getId());
    dbService.save(geoReferenceAssertion);
    assertNotNull(geoReferenceAssertion.getId());
  }

  @Test
  public void testFind() {
    GeoReferenceAssertion geoReferenceAssertion = GeoReferenceAssertionFactory.newGeoReferenceAssertion()
        .dwcDecimalLatitude(12.123456)
        .dwcDecimalLongitude(45.01)
        .dwcCoordinateUncertaintyInMeters(10)
        .build();
    dbService.save(geoReferenceAssertion);

    GeoReferenceAssertion fetchedGeoReferenceAssertion = dbService.find(GeoReferenceAssertion.class, geoReferenceAssertion.getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedGeoReferenceAssertion.getId());
    assertEquals(12.123456, fetchedGeoReferenceAssertion.getDwcDecimalLatitude());
    assertEquals(45.01, fetchedGeoReferenceAssertion.getDwcDecimalLongitude());
    assertEquals(10, fetchedGeoReferenceAssertion.getDwcCoordinateUncertaintyInMeters());
    assertNotNull(fetchedGeoReferenceAssertion.getCreatedOn());
  }
}
