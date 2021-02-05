package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SiteRepositoryIT extends CollectionModuleBaseIT {
  @Inject
  private SiteRepository siteRepository;
  private Site testSite;
  private final String TEST_SITE = "test site";

  private void createTestSite() throws ParseException {
    Polygon polygon = (Polygon) wktToGeometry("POLYGON ((0 0, 0 5, 5 5, 5 0, 0 0))");
    testSite = SiteFactory.newSite()
      .name(TEST_SITE)
      .polygon(polygon)
      .build();  
      service.save(testSite);
  }
  
  @BeforeEach
  public void setup() throws ParseException {
    createTestSite();
  }

  @Test
  public void findSite_whenNoFieldsAreSelected_SiteReturnedWithAllFields() {
    SiteDto siteDto = siteRepository.findOne(testSite.getUuid(), new QuerySpec(SiteDto.class));
    assertNotNull(siteDto);
    assertEquals(testSite.getUuid(), siteDto.getUuid());
    assertEquals(testSite.getCreatedBy(), siteDto.getCreatedBy());
    assertEquals(testSite.getName(), siteDto.getName());
    assertEquals(testSite.getPolygon(), siteDto.getPolygon());
  }

  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    SiteDto cg = new SiteDto();
    cg.setUuid(UUID.randomUUID());
    cg.setName(TEST_SITE);
    SiteDto result = siteRepository.findOne(siteRepository.create(cg).getUuid(), new QuerySpec(SiteDto.class));
    assertNotNull(result.getCreatedBy());
  }

  private Geometry wktToGeometry(String wellKnownText) throws ParseException {
    return new WKTReader().read(wellKnownText);
  }
          
}
