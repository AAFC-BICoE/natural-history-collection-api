package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.UUID;
import jakarta.inject.Inject;
import org.geolatte.geom.GeometryType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.service.SiteService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.SiteTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.collection.api.entities.Site;

@Import(SiteRepositoryIT.GeoLatteJacksonTestConfig.class)
public class SiteRepositoryIT extends BaseRepositoryIT {
  @Inject
  private SiteRepository siteRepository;

  @Inject
  private SiteService siteService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = { "aafc:user" })
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {
    SiteDto siteDto = SiteTestFixture.newSite();
    UUID siteUUID = createWithRepository(siteDto, siteRepository::onCreate);

    SiteDto result = siteRepository.getOne(siteUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(siteDto.getName(), result.getName());
    assertEquals(siteDto.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = { "notAAFC:user" })
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    Site testSite = SiteFactory.newSite()
        .group("preparation process definition")
        .name("aafc")
        .build();
    serviceTransactionWrapper.execute(siteService::create, testSite);

    SiteDto retrievedSite = siteRepository.getOne(testSite.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
        retrievedSite.getUuid(), CollectionManagedAttributeDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(retrievedSite));

    assertThrows(AccessDeniedException.class, () -> siteRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = { "aafc:user" })
  void create_WithPolygonSiteGeom_PersistsGeometry() throws Exception {
    SiteDto siteDto = SiteTestFixture.newSite();
    var attributes = JsonAPITestHelper.toAttributeMap(siteDto);
    attributes.put("siteGeom", SiteTestFixture.polygonGeoJson());
    JsonApiDocument document = JsonApiDocuments.createJsonApiDocument(
        null,
        SiteDto.TYPENAME,
        attributes);
    UUID siteUUID = createWithRepository(document, siteRepository::onCreate);
    SiteDto retrievedSite = siteRepository.getOne(siteUUID, "").getDto();
    assertEquals(GeometryType.POLYGON, retrievedSite.getSiteGeom().getGeometryType());
  }

  @TestConfiguration
  static class GeoLatteJacksonTestConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer geolatteCustomizer() {
      return builder -> builder.modulesToInstall(
          new org.geolatte.geom.json.GeolatteGeomModule());
    }
  }
}
