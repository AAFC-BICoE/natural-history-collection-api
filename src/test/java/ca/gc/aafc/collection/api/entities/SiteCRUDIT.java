package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgis.LinearRing;
import org.postgis.Polygon;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SiteCRUDIT extends CollectionModuleBaseIT {
  private final String TEST_SITE = "test site";

  @Test
  public void testSave() {
    Site site = SiteFactory.newSite()
       .polygon(new Polygon())
       .name(TEST_SITE)
       .build();
    assertNull(site.getId());
    service.save(site);
    assertNotNull(site.getId());
  }

  @Test
  public void testFind() throws SQLException{
    LinearRing linearRing = new LinearRing("0 0, 1 1, 1 2, 1 1, 0 0");
    Polygon polygon = new Polygon(new LinearRing[]{linearRing});
    Site site = SiteFactory.newSite()
        .name(TEST_SITE)
        .polygon(polygon)
        .build();
    service.save(site);

    Site fetchedSite = service.find(Site.class, site.getId());
    assertEquals(site.getId(), fetchedSite.getId());
    assertEquals(site.getName(), fetchedSite.getName());
    assertEquals(site.getPolygon(), fetchedSite.getPolygon());
    assertNotNull(fetchedSite.getCreatedOn());
  }
}
