package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SiteCRUDIT extends CollectionModuleBaseIT {
  private final String TEST_SITE = "test site";
  private Polygon testPolygon ; 

  @BeforeEach
  private void setUp() throws ParseException {
    testPolygon = (Polygon) wktToGeometry("POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))");
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
  public void testFind() throws ParseException{
    
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

  private Geometry wktToGeometry(String wellKnownText) throws ParseException {
    return new WKTReader().read(wellKnownText);
  }
}
