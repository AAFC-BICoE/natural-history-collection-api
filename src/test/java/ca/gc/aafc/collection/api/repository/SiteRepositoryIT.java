package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import io.crnk.core.queryspec.QuerySpec;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.codec.Wkt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.io.WKTReader;

import javax.inject.Inject;



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

  private void createTestSite() {
    Polygon polygon = (Polygon) Wkt.fromWkt("POLYGON (" +
    "(30 10, 40 40, 20 40, 10 20, 30 10), " +
    "(20 30, 35 35, 30 20, 20 30))");
    testSite = SiteFactory.newSite()
      .name(TEST_SITE)
      .polygon(polygon)
      .build();  
      service.save(testSite);
  }
  
  @BeforeEach
  public void setup() { 
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
    cg.setName(TEST_SITE + "2");
    SiteDto result = siteRepository.findOne(siteRepository.create(cg).getUuid(), new QuerySpec(SiteDto.class));
    assertNotNull(result.getCreatedBy());
  }
}
