package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.config.TestConfigProperties;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.SiteTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

@SpringBootTest(classes = CollectionModuleApiLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = PostgresTestContainerInitializer.class)
@Import(TestConfigProperties.class)
public class SiteOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = SiteDto.TYPENAME;

  protected SiteOpenApiIT() {
    super("/api/v1/");
  }

  @Test
  @SneakyThrows
  void site_SpecValid() {

    // Create DTO using fixture
    SiteDto siteDto = SiteTestFixture.newSite();

    siteDto.setCreatedBy("test user");
    siteDto.setAttachment(null);

    // Convert DTO attributes to map and override siteGeom with GeoJSON map
    Map<String, Object> attributeMap = JsonAPITestHelper.toAttributeMap(siteDto);
    attributeMap.put("siteGeom", SiteTestFixture.polygonGeoJson());

    // Validate API response against OpenAPI schema
    OpenAPI3Assertions.assertRemoteSchema(
        OpenAPIConstants.COLLECTION_API_SPECS_URL,
        "Site",
        sendPost(
            TYPE_NAME,
            JsonAPITestHelper.toJsonAPIMap(
                TYPE_NAME,
                attributeMap,
                Map.of(
                    "attachment",
                    JsonAPITestHelper.generateExternalRelationList("metadata", 1)),
                null))
            .extract().asString());
  }
}
