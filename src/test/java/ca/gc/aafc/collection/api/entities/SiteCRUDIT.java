package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.builder.DSL;
import org.geolatte.geom.crs.CoordinateReferenceSystems;
import org.junit.jupiter.api.Test;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SiteCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "dina-group";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private static final String EXPECTED_CODE = "LTAE-M";
  private static final int EXPECTED_SITE_GEOM_SRID = 4326;
  private static final Polygon<G2D> EXPECTED_SITE_GEOM = DSL.polygon(
      CoordinateReferenceSystems.WGS84,
      // Outer ring
      DSL.ring(
          DSL.g(100.0, 0.0),
          DSL.g(101.0, 0.0),
          DSL.g(101.0, 1.0),
          DSL.g(100.0, 1.0),
          DSL.g(100.0, 0.0)),
      // Inner ring (hole)
      DSL.ring(
          DSL.g(102.0, 2.0),
          DSL.g(108.0, 2.0),
          DSL.g(108.0, 8.0),
          DSL.g(102.0, 8.0),
          DSL.g(102.0, 2.0)));
  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescriptionFactory
      .newMultilingualDescription();
  private final List<UUID> EXPECT_ATTACHMENTS = List.of(UUID.randomUUID(), UUID.randomUUID());

  @Test
  void create() {
    Site site = buildExpectedSite();

    siteService.create(site);

    assertNotNull(site.getId());
    assertNotNull(site.getCreatedOn());
    assertNotNull(site.getUuid());
  }

  @Test
  void find() {
    Site site = buildExpectedSite();

    siteService.create(site);

    Site result = siteService.findOne(
        site.getUuid(),
        Site.class);
    assertEquals(EXPECTED_NAME, result.getName());
    assertEquals(EXPECTED_GROUP, result.getGroup());
    assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    assertEquals(EXPECTED_CODE, result.getCode());
    assertNotNull(result.getSiteGeom());
    assertEquals(4326, result.getSiteGeom().getSRID());
    assertEquals(EXPECTED_SITE_GEOM.getExteriorRing().getPositions(),
        result.getSiteGeom().getExteriorRing().getPositions());
    assertEquals(1, result.getSiteGeom().getNumInteriorRing());
    assertEquals(
        EXPECTED_SITE_GEOM.getInteriorRingN(0).getPositions(),
        result.getSiteGeom().getInteriorRingN(0).getPositions());

    assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(),
        result.getMultilingualDescription().getDescriptions());
    assertEquals(EXPECT_ATTACHMENTS, result.getAttachment());
  }

  private Site buildExpectedSite() {
    return SiteFactory.newSite()
        .name(EXPECTED_NAME)
        .group(EXPECTED_GROUP)
        .createdBy(EXPECTED_CREATED_BY)
        .multilingualDescription(MULTILINGUAL_DESCRIPTION)
        .code(EXPECTED_CODE)
        .siteGeom(EXPECTED_SITE_GEOM)
        .attachment(EXPECT_ATTACHMENTS)
        .build();
  }
}
