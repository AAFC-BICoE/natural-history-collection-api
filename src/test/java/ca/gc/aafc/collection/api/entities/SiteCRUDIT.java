package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;

import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SiteCRUDIT extends CollectionModuleBaseIT {
  private final String TEST_SITE = "test site";
  private Polygon testPolygon ; 

  @BeforeEach
  private void setUp() {//throws ParseException {
    testPolygon = (Polygon) Wkt.fromWkt("POLYGON (" +
    "(30 10, 40 40, 20 40, 10 20, 30 10), " +
    "(20 30, 35 35, 30 20, 20 30))");
  }

  @Test
  public void testSave() {
    Site site = SiteFactory.newSite()
       .polygon(testPolygon)
       .name(TEST_SITE)
       .build();
    assertNull(site.getId());
    service.save(site);
    assertNotNull(site.getId());
  }

  @Test
  public void testFind() { 
    
    Site site = SiteFactory.newSite()
        .name(TEST_SITE)
        .polygon(testPolygon)
        .build();
    service.save(site);

    Site fetchedSite = service.find(Site.class, site.getId());
    assertEquals(site.getId(), fetchedSite.getId());
    assertEquals(site.getName(), fetchedSite.getName());
    assertEquals(site.getPolygon(), fetchedSite.getPolygon());
    assertNotNull(fetchedSite.getCreatedOn());
  }
}
